package pulleydoreurae.careerquestbackend.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserFirstAddInfo;

/**
 *  회원가입 후 첫 로그인할 때 추가 정보 입력이 필요한 유저에 관한 Repository
 */
public interface UserFirstAddInfoRepository extends JpaRepository<UserFirstAddInfo, Long> {
    boolean existsByUserId(String userId);
    void deleteByUserId(String userId);
}
