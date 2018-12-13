echo "Testing to deploy generated site docs"

setup_git() {
  git config user.email "travis@travis-ci.org"
  git config user.name "Travis CI"
}

commit_files() {
  git status
  git add docs/*
  git status
  git commit --message "Generated site documentation... Travis build: $TRAVIS_BUILD_NUMBER"
  git status
}

upload_files() {
  git push --quiet "https://$GITHUB_TOKEN@github.com/e-pettersson-ericsson/eiffel-intelligence-frontend.git" > /dev/null 2>&1
}


# setup_git
commit_files
upload_files
