@echo off
javac -d target/test-classes src/test/java/com/nasarover/service/SimpleTest.java
java -cp target/test-classes com.nasarover.service.SimpleTest 