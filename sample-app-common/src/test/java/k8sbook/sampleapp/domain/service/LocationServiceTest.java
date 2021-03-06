package k8sbook.sampleapp.domain.service;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import k8sbook.sampleapp.domain.model.Location;
import k8sbook.sampleapp.domain.model.Region;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.assertj.db.api.Assertions.assertThat;

@SpringBootTest
public class LocationServiceTest {

    @Autowired
    private LocationService service;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Test
    @Tag("DBRequired")
    public void testRegisterLocations() {
        var locationList = List.of(
                new Location("지점5", new Region(1, "지역1", LocalDateTime.now()), "지점5의 상세입니다."),
                new Location("지점6", new Region(1, "지역1", LocalDateTime.now()), "지점6의 상세입니다.")
        );
        service.registerLocations(locationList);

        var locationTable = new Table(dataSource, "location");
        assertThat(locationTable).hasNumberOfRows(6);
    }

    @BeforeEach
    public void prepareDatabase() {
        var operations = sequenceOf(
                deleteAllFrom("location"),
                deleteAllFrom("region"),
                insertInto("region")
                        .columns("region_id", "region_name", "creation_timestamp")
                        .values(1, "지역1", LocalDateTime.now())
                        .values(2, "지역2", LocalDateTime.now())
                        .values(3, "지역3", LocalDateTime.now())
                        .values(4, "지역4", LocalDateTime.now())
                        .build(),
                insertInto("location")
                        .columns("location_id", "location_name", "region_id", "note")
                        .values(1, "지점1", 1, "지점1의 상세입니다.")
                        .values(2, "지점2", 1, "지점2의 상세입니다.")
                        .values(3, "지점3", 1, "지점3의 상세입니다.")
                        .values(4, "지점4", 1, "지점4의 상세입니다.")
                        .build()
        );
        var dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
        dbSetup.launch();
    }

}
