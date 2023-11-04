package stg.payit.wallet.appuser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import stg.payit.wallet.device.Device;

import javax.persistence.*;

import java.security.SecureRandom;
import java.util.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;


@EqualsAndHashCode
@NoArgsConstructor
@Data
@Entity
public class AppUser implements UserDetails {
	@SequenceGenerator(name = "student_sequence", sequenceName = "student_sequence", allocationSize = 1)
	@Id
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_sequence")
	private Long id;
	private String email;
	@Column(unique = true)
	private String cin;

	private String firstName;
	private String lastName;
	@Column(nullable = true)
	private String fcm_token;
	@Column(unique = true)
	private String phoneNumber;
	private double solde;
	//@Column(unique = true)
//	private String rib;
/*	@Column(unique = true)
	private String deviceId; // ID du téléphone*/

	private String longitude;
	private String Latitude;
	@Column(unique = true)
	private String deviceId;
	private String secret;
	private Date loginTime;
	@JsonIgnore
	private String password;
	@Enumerated(EnumType.STRING)
	private AppUserRole appUserRole;
	private Boolean locked = false;
	private Boolean enabled = false;
	private String gender;
	private String question1;
	private String question2;
	private String question3;
	//	@OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
	@OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@JsonIgnore
	private List<Device> devices = new ArrayList<>();

	public void addDevice(Device device) {
		this.devices.add(device);
		device.setAppUser(this);
	}
	public AppUser(String firstName, String lastName, String email, String password, AppUserRole appUserRole,
			String phoneNumber,String cin, String gender, String deviceId, String longitude,String latitude, Date loginTime,String secret,String question1,String question2,String question3) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.appUserRole = appUserRole;
		this.fcm_token = null;
		this.phoneNumber = phoneNumber;
	//this.rib="SGM"+randomString(10);
		this.solde = 0;		
		this.cin = cin;
		this.gender=gender;
//		this.deviceId=deviceId;this.longitude = longitude;
//		this.Latitude=latitude;
//		this.loginTime = loginTime;
		this.deviceId = deviceId;
		this.longitude = longitude;
		this.Latitude=latitude;
		this.loginTime = loginTime;
		this.secret=secret;
		this.question1 = question1;
		this.question2 = question2;
		this.question3 = question3;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(appUserRole.name());
		return Collections.singletonList(authority);
	}

	@Override
	public String getPassword() {
		return password;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !locked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return phoneNumber;
	}

	static final String SOURCE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static SecureRandom secureRnd = new SecureRandom();
	public static String randomString(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++)
			sb.append(SOURCE.charAt(secureRnd.nextInt(SOURCE.length())));
		return sb.toString();
	}
}
