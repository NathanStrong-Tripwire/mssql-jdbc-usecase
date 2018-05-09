import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import net.sourceforge.jtds.jdbcx.JtdsDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

/**
 * Before you run this class, you must ensure the SQL server is properly configured. See README.md for
 * details.
 */
public class SqlServerUseCase {
    private static final String SQLSERVER_FQDN = "fqdn.of.your.sqlserver.host";
    private static final String SQLSERVER_DB = "your-database-name";
    private static final int SQLSERVER_PORT = 1433;
    /**
     * The domain user account information.
     */
    private static final String SQL_DOMAIN = "DOMAIN";
    private static final String SQL_USER = "user";
    private static final String SQL_PASSWORD = "domain-user-password";

    public static void main(String[] args) {
        try {
            Stream.of(getJtdsDataSource(), getMicrosoftDataSource()).forEach((ds) -> {
                try (Connection conn = ds.getConnection()) {
                    conn.setAutoCommit(true);

                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("DROP TABLE IF EXISTS foo");
                        stmt.execute("CREATE TABLE foo (bar int)");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            System.out.println("PASS");
        } catch (RuntimeException ex) {
            System.out.println("FAIL");
            ex.printStackTrace();
        }
    }

    /**
     * This is the function of jTDS that we rely on. Assuming DOMAIN\user is a valid user, jTDS can
     * authenticate as DOMAIN\user even if I'm logged onto DOMAIN as user2, or I'm not logged into the
     * domain at all, or I'm on a Linux system.
     */
    private static DataSource getJtdsDataSource() {
        JtdsDataSource ds = new JtdsDataSource();
        ds.setServerName(SQLSERVER_FQDN);
        ds.setPortNumber(1433);
        ds.setAppName(SqlServerUseCase.class.getSimpleName());
        ds.setDatabaseName(SQLSERVER_DB);
        ds.setDomain(SQL_DOMAIN);
        ds.setUser(SQL_USER);
        ds.setPassword(SQL_PASSWORD);
        ds.setUseNTLMV2(true);

        return ds;
    }

    /**
     * This is supposed to configure an equivalent DataSource as in the above {@link #getJtdsDataSource()} method.
     * Note that I have also tried using user@domain format in the setUser() method, to no avail. Also, this needs
     * to function cross-platform (that is, it can't depend on sqljdbc_auth.dll being present to function correctly).
     */
    private static DataSource getMicrosoftDataSource() {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName(SQLSERVER_FQDN);
        ds.setPortNumber(SQLSERVER_PORT);
        ds.setApplicationName(SqlServerUseCase.class.getSimpleName());
        ds.setDatabaseName(SQLSERVER_DB);
        ds.setUser(SQL_DOMAIN + "\\" + SQL_USER);
        ds.setPassword(SQL_PASSWORD);
        ds.setAuthentication("ActiveDirectoryPassword");

        return ds;
    }
}
