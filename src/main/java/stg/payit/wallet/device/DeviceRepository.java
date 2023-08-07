package stg.payit.wallet.device;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.payit.wallet.device.Device;

public interface DeviceRepository extends JpaRepository<Device,Long> {


}
