package cam.slava.learn.repository;

import cam.slava.learn.entity.TaskEntity;
import cam.slava.learn.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findAllByUserEntity_Id(Long userId);
}
