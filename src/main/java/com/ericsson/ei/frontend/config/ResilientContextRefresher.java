package com.ericsson.ei.frontend.config;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.refresh.ConfigDataContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * A resilient ContextRefresher that recovers from Jasypt DecryptionException.
 *
 * When a corrupted ENC() value is loaded via /actuator/refresh, the in-memory
 * property source retains the bad value. On the next refresh attempt,
 * getCurrentEnvironmentProperties() tries to decrypt it, throws, and aborts
 * before updateEnvironment() can reload the corrected file.
 *
 * This class catches that failure, forces updateEnvironment() to run (reloading
 * from disk), then retries the normal flow.
 */
public class ResilientContextRefresher extends ConfigDataContextRefresher {

    private static final Logger LOG = LoggerFactory.getLogger(ResilientContextRefresher.class);

    public ResilientContextRefresher(ConfigurableApplicationContext context,
                                     RefreshScope scope) {
        super(context, scope);
    }

    public ResilientContextRefresher(ConfigurableApplicationContext context,
                                     RefreshScope scope,
                                     RefreshAutoConfiguration.RefreshProperties properties) {
        super(context, scope, properties);
    }

    @Override
    public synchronized Set<String> refreshEnvironment() {
        try {
            return super.refreshEnvironment();
        } catch (Exception e) {
            if (isDecryptionFailure(e)) {
                LOG.warn("Refresh failed due to undecryptable property. Forcing environment reload from disk and retrying. Error: {}", e.getMessage());
                updateEnvironment();
                return retryRefreshEnvironment();
            }
            throw e;
        }
    }

    private Set<String> retryRefreshEnvironment() {
        try {
            return super.refreshEnvironment();
        } catch (Exception e) {
            LOG.error("Refresh retry also failed. If the file still has a corrupted ENC() value, correct it and try again. Error: {}", e.getMessage());
            throw e;
        }
    }

    private boolean isDecryptionFailure(Throwable e) {
        while (e != null) {
            String name = e.getClass().getName();
            if (name.contains("DecryptionException")
                    || name.contains("EncryptionOperationNotPossibleException")) {
                return true;
            }
            String msg = e.getMessage();
            if (msg != null && msg.contains("Decryption of Properties failed")) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }
}
