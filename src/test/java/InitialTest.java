import com.libmgrsys.DatabaseHelper;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;

public class InitialTest
{
    @Test
    void connectionTest()
    {
        Connection con = DatabaseHelper.connect();
    }

    @Test
    void resultSetTest()
    {
        ResultSet rs = DatabaseHelper.createResultSet(DatabaseHelper.connect(), "SELECT * FROM users");
    }
}
