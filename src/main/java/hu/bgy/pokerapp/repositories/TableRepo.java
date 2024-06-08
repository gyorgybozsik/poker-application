package hu.bgy.pokerapp.repositories;
import hu.bgy.pokerapp.models.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepo extends JpaRepository <Table, Long> {

}
