#!/usr/bin/env bash
set -e pipefail

DEPENDENCY_PACKAGE="gson-version"
DEPENDENCY_PACKAGE_VERSION="2.7.0"

if [[ ! -x "$(command -v git)" ]]; then
  echo "Please install git."
fi
if [[ ! -x "$(command -v docker)" ]]; then
  echo "Please install docker."
fi
docker ps -a

WORK_DIR="/opt/git"
REPO_NAME="target_of_update_maven"
REPO_DIR=${WORK_DIR}"/"${REPO_NAME}
M2_DIR="/opt/git/.m2"

if [ ! -d "${M2_DIR}" ]; then
  echo "ERROR! ${REPO_DIR} not exists."
  exit 1
fi

if [ -d "${REPO_DIR}" ]; then
  echo "${REPO_DIR} exists, remove and re-clone it."
  rm -rf ${REPO_DIR}
fi

git clone -b develop git@github.com:ajdfajdfl2003/target_of_update_maven.git ${REPO_DIR}

docker run --rm \
  --name "maven-versionUpdate-$(date +"%s")" \
  -v "${M2_DIR}:/root/.m2" \
  -v "${WORK_DIR}:${WORK_DIR}" \
  -w "${REPO_DIR}" \
  maven:3.6.3-jdk-8 mvn versions:set-property -Dproperty=${DEPENDENCY_PACKAGE} -DnewVersion=${DEPENDENCY_PACKAGE_VERSION}

cd ${REPO_DIR}
git add .
git commit -m "update ${DEPENDENCY_PACKAGE} to ${DEPENDENCY_PACKAGE_VERSION}"
git push origin develop:develop
