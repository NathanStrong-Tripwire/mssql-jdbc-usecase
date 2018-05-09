# mssql-jdbc-usecase

A simple demonstration of the use case we are trying to use.

The use case should be pretty self-evident from the code alone; however, if you 
want to actually run this code, there's some legwork to do:

1. Install SQL Server 2016 or newer and set it up to allow TLS connections.

   The TLS part is important, because the Microsoft driver requires SSL for any form of
   authentication that isn't basic sql authentication. SQL Server 2016 or newer is
   required because older versions do not provide the `DROP TABLE IF EXISTS` 
   functionality.
   
2. Create a database that can be accessed by a Windows user, and grant that user
   both CREATE TABLE and DROP TABLE privileges.
   
   Note that the Windows account you grant access to should NOT be your own Windows
   account. This is important for our use case.

3. Import the SQL Server's TLS certificate into your JRE's trust store.

   I suggest making a copy of `$JAVA_HOME/jre/lib/security/cacerts` and importing the
   certificate into it (the keystore passphrase is 'changeit', yes really), and then
   passing `-Djavax.net.ssl.trustStore=<path>` (where `<path>` is the cacerts copy
   you made) to the JRE.
   
4. Edit the code to fill in the details.

   I put everything in static constants at the top, so just fill them in with the
   appropriate values.
   
5. Compile and run.

## Building

This project uses the applications gradle plug-in.

    ./gradlew distZip
    
This will create a zip file in build/distributions/

## Running

1. Unzip the zip file created by the build process
2. If you made a copy of cacerts as described in step 3 above, add the JVM parameter
   to the bin/mssql-jdbc-usecase script(s).
3. run the `bin/mssql-jdbc-usecase` script to run the application.
   
Expected behavior: if the Microsoft JDBC and jTDS provided equivalent functionality, 
it would output `PASS`
Actual behavior: `FAIL` followed by a stack trace.
