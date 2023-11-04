package stg.payit.wallet.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;
@CrossOrigin("*")
@Repository
@Transactional(readOnly = true)
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
	Optional<AppUser> findByCin(String cin);
	Optional<AppUser> findByEmail(String email);

//   Optional<AppUser> findByUsername(String username);
    @Modifying
    @Transactional
    @Query("UPDATE AppUser u SET u.secret = :secret WHERE u.email = :email")
    void updateSecretByEmail(@Param("email") String email, @Param("secret") String secret);
    @Query("SELECT u.secret FROM AppUser u WHERE u.email = :email")
    Optional<String> findSecretByEmail(@Param("email") String email);
    @Transactional(readOnly = true)
    @Query("SELECT u.email FROM AppUser u WHERE u.phoneNumber = ?1")
    Optional<String> findEmailByPhoneNumber(String phoneNumber);
    Optional<AppUser> findByPhoneNumber(String phoneNumber);

    @Query("SELECT d.deviceId FROM AppUser u JOIN u.devices d WHERE u.phoneNumber = :phoneNumber")
    List<String> findDeviceIdByPhoneNumber(@Param("phoneNumber") String phoneNumber);
  /*  @Transactional(readOnly = true)
    @Query("SELECT u.deviceId FROM AppUser u WHERE u.phoneNumber = ?1")
    Optional<String> findDeviceIdByPhoneNumber(String phoneNumber);*/
/*  @Query("SELECT d.deviceId FROM AppUser u JOIN u.devices d WHERE u.phoneNumber = :phoneNumber")
  List<String> findDeviceIdByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    Optional<AppUser> findByPhoneNumber(String phone_number);*/

//    Optional<AppUser> findByPhoneNumber(String phoneNumber);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a " + "SET a.enabled = 1 WHERE a.email = ?1")
    int enableAppUser(String email);

    @Transactional
    @Modifying
    @Query("SELECT u FROM AppUser u WHERE u.fcm_token IS NOT NULL")
    List<AppUser> finAllUsers();

    @Query("SELECT u FROM AppUser u WHERE u.gender= ?1")
    List<AppUser> findBygenderHomme(String gender);

}
