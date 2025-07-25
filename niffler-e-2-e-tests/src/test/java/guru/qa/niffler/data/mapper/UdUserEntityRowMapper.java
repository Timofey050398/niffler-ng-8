package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.entity.currency.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UdUserEntityRowMapper implements RowMapper<UserEntity> {

    public static final UdUserEntityRowMapper instance = new UdUserEntityRowMapper();

    private UdUserEntityRowMapper() {
    }

    @Override
    public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserEntity result = new UserEntity();
        result.setId(rs.getObject("id", UUID.class));
        result.setUsername(rs.getString("username"));
        result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        result.setFirstname(rs.getString("firstname"));
        result.setSurname(rs.getString("surname"));
        result.setFullname(rs.getString("full_name"));
        result.setPhoto(rs.getBytes("photo"));
        result.setPhotoSmall(rs.getBytes("photo_small"));
        return result;
    }
}