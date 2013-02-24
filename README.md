maven_repo_clean
================

A short term workaround for the maven bug MNG-4142.

It will check all the maven-metadata-local.xml files in the supplied paths of the
local Maven repository and change all localCopy values from true to false.

