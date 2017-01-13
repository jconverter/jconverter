#!/usr/bin/env bash

PROJECT=jconverter
VERSION=0.0.1
gpg -ab $PROJECT-$VERSION.pom
gpg -ab $PROJECT-$VERSION.jar
gpg -ab $PROJECT-$VERSION-sources.jar
gpg -ab $PROJECT-$VERSION-javadoc.jar

jar -cvf bundle.jar $PROJECT-$VERSION.pom $PROJECT-$VERSION.pom.asc $PROJECT-$VERSION.jar $PROJECT-$VERSION.jar.asc $PROJECT-$VERSION-javadoc.jar $PROJECT-$VERSION-javadoc.jar.asc $PROJECT-$VERSION-sources.jar $PROJECT-$VERSION-sources.jar.asc