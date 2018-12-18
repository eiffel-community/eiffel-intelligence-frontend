echo "Executing deploy script to generate site docs"

commit_files() {
  git status
  git add docs/*
  git commit --message "Generated site documentation. Travis build: $TRAVIS_BUILD_NUMBER [skip ci]"
}

upload_files() {
  git remote add origin "https://$GITHUB_TOKEN@github.com/e-pettersson-ericsson/eiffel-intelligence-frontend.git" > /dev/null 2>&1
  git remote -v
  git push --quiet origin HEAD:master
}


commit_files
upload_files
