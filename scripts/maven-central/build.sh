#!/usr/bin/env bash

PROJECT=jconverter
VERSION=0.0.2

JAR_DIR=../../target
cp $JAR_DIR/$PROJECT-$VERSION.jar $PROJECT-$VERSION.jar
cp $JAR_DIR/$PROJECT-$VERSION-javadoc.jar $PROJECT-$VERSION-javadoc.jar
cp $JAR_DIR/$PROJECT-$VERSION-sources.jar $PROJECT-$VERSION-sources.jar

POM_DIR=../..
cp $POM_DIR/pom.xml $PROJECT-$VERSION.pom

gpg -ab $PROJECT-$VERSION.pom
gpg -ab $PROJECT-$VERSION.jar
gpg -ab $PROJECT-$VERSION-sources.jar
gpg -ab $PROJECT-$VERSION-javadoc.jar

jar -cvf bundle.jar $PROJECT-$VERSION.pom $PROJECT-$VERSION.pom.asc $PROJECT-$VERSION.jar $PROJECT-$VERSION.jar.asc $PROJECT-$VERSION-javadoc.jar $PROJECT-$VERSION-javadoc.jar.asc $PROJECT-$VERSION-sources.jar $PROJECT-$VERSION-sources.jar.asc