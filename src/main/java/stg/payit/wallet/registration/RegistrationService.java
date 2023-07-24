package stg.payit.wallet.registration;

import lombok.AllArgsConstructor;
import stg.payit.wallet.appuser.AppUser;
import stg.payit.wallet.appuser.AppUserRepository;
import stg.payit.wallet.appuser.AppUserRole;
import stg.payit.wallet.appuser.AppUserService;
import stg.payit.wallet.email.EmailSender;
import stg.payit.wallet.registration.token.ConfirmationToken;
import stg.payit.wallet.registration.token.ConfirmationTokenService;
import stg.payit.wallet.responseHandler.ResponseHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@CrossOrigin("*")
@Service
@AllArgsConstructor
public class RegistrationService {
	private final AppUserService appUserService;
	private final EmailValidator emailValidator;
	private final ConfirmationTokenService confirmationTokenService;
	private final EmailSender emailSender;

	public ResponseEntity<Object> register(RegistrationRequest request) {
		boolean isValidEmail = emailValidator.test(request.getEmail());

		if (!isValidEmail) {
			return ResponseHandler.generateResponseString("Email Not Valid", HttpStatus.OK);
		}
		AppUser user = new AppUser(request.getFirstName(), request.getLastName(), request.getEmail(),
				request.getPassword(), AppUserRole.USER,request.getPhoneNumber(),request.getCin(),request.getGender());
		String token = appUserService.signUpUser(user);
		String link = "http://192.168.1.38:8040/wallet_war/registration/confirm?token=" + token;
		emailSender.send(request.getEmail(), buildEmail(request.getFirstName(), link));

		return ResponseHandler.generateResponse("Registrated!", HttpStatus.OK,user);
	}
	@Transactional
	public ResponseEntity<Object> confirmToken(String token) {
		ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
				.orElseThrow(() -> new IllegalStateException("token not found"));

		if (confirmationToken.getConfirmedAt() != null) {
			return ResponseHandler.generateResponseString("email already confirmed", HttpStatus.ALREADY_REPORTED);
		}

		LocalDateTime expiredAt = confirmationToken.getExpiresAt();

		if (expiredAt.isBefore(LocalDateTime.now())) {
			return ResponseHandler.generateResponseString("Token Expired", HttpStatus.NOT_ACCEPTABLE);
		}

		confirmationTokenService.setConfirmedAt(token);
		appUserService.enableAppUser(confirmationToken.getAppUser().getEmail());
		return ResponseHandler.generateResponseString("Email Confirmed", HttpStatus.OK);
	}
		
	
	

		private String buildEmail(String name, String link) {
		return "\r\n"
				+ "\r\n"
				+ "<!DOCTYPE html>\r\n"
				+ "<html xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" lang=\"en\">\r\n"
				+ "\r\n"
				+ "<head>\r\n"
				+ "	<title></title>\r\n"
				+ "	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n"
				+ "	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
				+ "	<!--[if mso]><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch><o:AllowPNG/></o:OfficeDocumentSettings></xml><![endif]-->\r\n"
				+ "	<!--[if !mso]><!-->\r\n"
				+ "	<link href=\"https://fonts.googleapis.com/css?family=Abril+Fatface\" rel=\"stylesheet\" type=\"text/css\">\r\n"
				+ "	<link href=\"https://fonts.googleapis.com/css?family=Cabin\" rel=\"stylesheet\" type=\"text/css\">\r\n"
				+ "	<link href=\"https://fonts.googleapis.com/css?family=Ubuntu\" rel=\"stylesheet\" type=\"text/css\">\r\n"
				+ "	<!--<![endif]-->\r\n"
				+ "	<style>\r\n"
				+ "		* {\r\n"
				+ "			box-sizing: border-box;\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		body {\r\n"
				+ "			margin: 0;\r\n"
				+ "			padding: 0;\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		a[x-apple-data-detectors] {\r\n"
				+ "			color: inherit !important;\r\n"
				+ "			text-decoration: inherit !important;\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		#MessageViewBody a {\r\n"
				+ "			color: inherit;\r\n"
				+ "			text-decoration: none;\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		p {\r\n"
				+ "			line-height: inherit\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		.desktop_hide,\r\n"
				+ "		.desktop_hide table {\r\n"
				+ "			mso-hide: all;\r\n"
				+ "			display: none;\r\n"
				+ "			max-height: 0px;\r\n"
				+ "			overflow: hidden;\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "		@media (max-width:620px) {\r\n"
				+ "			.desktop_hide table.icons-inner {\r\n"
				+ "				display: inline-block !important;\r\n"
				+ "			}\r\n"
				+ "\r\n"
				+ "			.icons-inner {\r\n"
				+ "				text-align: center;\r\n"
				+ "			}\r\n"
				+ "\r\n"
				+ "			.icons-inner td {\r\n"
				+ "				margin: 0 auto;\r\n"
				+ "			}\r\n"
				+ "\r\n"
				+ "			.fullMobileWidth,\r\n"
				+ "			.image_block img.big,\r\n"
				+ "			.row-content {\r\n"
				+ "				width: 100% !important;\r\n"
				+ "			}\r\n"
				+ "\r\n"
				+ "			.mobile_hide {\r\n"
				+ "				display: none;\r\n"
				+ "			}\r\n"
				+ "\r\n"
				+ "			.stack .column {\r\n"
				+ "				width: 100%;\r\n"
				+ "				display: block;\r\n"
				+ "			}\r\n"
				+ "\r\n"
				+ "			.mobile_hide {\r\n"
				+ "				min-height: 0;\r\n"
				+ "				max-height: 0;\r\n"
				+ "				max-width: 0;\r\n"
				+ "				overflow: hidden;\r\n"
				+ "				font-size: 0px;\r\n"
				+ "			}\r\n"
				+ "\r\n"
				+ "			.desktop_hide,\r\n"
				+ "			.desktop_hide table {\r\n"
				+ "				display: table !important;\r\n"
				+ "				max-height: none !important;\r\n"
				+ "			}\r\n"
				+ "		}\r\n"
				+ "	</style>\r\n"
				+ "</head>\r\n"
				+ "\r\n"
				+ "<body style=\"background-color: transparent; margin: 0; padding: 0; -webkit-text-size-adjust: none; text-size-adjust: none;\">\r\n"
				+ "	<table class=\"nl-container\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: transparent; background-size: auto; background-image: none; background-position: top left; background-repeat: no-repeat;\">\r\n"
				+ "		<tbody>\r\n"
				+ "			<tr>\r\n"
				+ "				<td>\r\n"
				+ "					<table class=\"row row-1\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #132437;\">\r\n"
				+ "						<tbody>\r\n"
				+ "							<tr>\r\n"
				+ "								<td>\r\n"
				+ "									<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-position: center top; color: #000000; background-image: url('https://d1oco4z2z1fhwp.cloudfront.net/templates/default/4011/blue-glow_3.jpg'); background-repeat: no-repeat; width: 600px;\" width=\"600\">\r\n"
				+ "										<tbody>\r\n"
				+ "											<tr>\r\n"
				+ "												<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\r\n"
				+ "													<table class=\"image_block\" width=\"100%\" border=\"0\" cellpadding=\"10\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
				+ "														<tr>\r\n"
				+ "															<td>\r\n"
				+ "																<div align=\"center\" style=\"line-height:10px\"><img src=\"https://d15k2d11r6t6rl.cloudfront.net/public/users/Integrators/BeeProAgency/820185_804124/PAYIT%20%285%29.png\" style=\"display: block; height: auto; border: 0; width: 120px; max-width: 100%;\" width=\"120\"></div>\r\n"
				+ "															</td>\r\n"
				+ "														</tr>\r\n"
				+ "													</table>\r\n"
				+ "													<table class=\"heading_block\" width=\"100%\" border=\"0\" cellpadding=\"25\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
				+ "														<tr>\r\n"
				+ "															<td>\r\n"
				+ "																<h1 style=\"margin: 0; color: #86b61b; font-size: 36px; font-family: 'Ubuntu', Tahoma, Verdana, Segoe, sans-serif; line-height: 120%; text-align: center; direction: ltr; font-weight: 400; letter-spacing: normal; margin-top: 0; margin-bottom: 0;\">&nbsp;Please Confirm your email address to finish setting up your account.&nbsp;</h1>\r\n"
				+ "															</td>\r\n"
				+ "														</tr>\r\n"
				+ "													</table>\r\n"
				+ "												</td>\r\n"
				+ "											</tr>\r\n"
				+ "										</tbody>\r\n"
				+ "									</table>\r\n"
				+ "								</td>\r\n"
				+ "							</tr>\r\n"
				+ "						</tbody>\r\n"
				+ "					</table>\r\n"
				+ "					<table class=\"row row-2\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #132437;\">\r\n"
				+ "						<tbody>\r\n"
				+ "							<tr>\r\n"
				+ "								<td>\r\n"
				+ "									<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-position: center top; color: #000000; width: 600px;\" width=\"600\">\r\n"
				+ "										<tbody>\r\n"
				+ "											<tr>\r\n"
				+ "												<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\r\n"
				+ "													<table class=\"button_block\" width=\"100%\" border=\"0\" cellpadding=\"30\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
				+ "														<tr>\r\n"
				+ "															<td>\r\n"
				+ "																<div align=\"center\">\r\n"
				+ "																	<!--[if mso]><v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" href=\"https://www.canva.com/design/DAFDBkZwOis/sS8LjqbxrriKlu5-IUcdDQ/edit\" style=\"height:45px;width:247px;v-text-anchor:middle;\" arcsize=\"23%\" strokeweight=\"4.5pt\" strokecolor=\"#86B61B\" fillcolor=\"#0068a5\"><w:anchorlock/><v:textbox inset=\"0px,0px,0px,0px\"><center style=\"color:#ffffff; font-family:Arial, sans-serif; font-size:16px\"><![endif]-->\r\n"
				+ "																	<a href="+link+"\r\n"
				+ "																	target=\"_blank\" style=\"text-decoration:none;display:inline-block;color:#ffffff;background-color:#0068a5;border-radius:10px;width:auto;border-top:6px solid #86B61B;font-weight:400;border-right:1px solid #0068a5;border-bottom:1px solid #0068a5;border-left:1px solid #0068a5;padding-top:10px;padding-bottom:10px;font-family:Arial, Helvetica Neue, Helvetica, sans-serif;text-align:center;mso-border-alt:none;word-break:keep-all;\"><span style=\"padding-left:60px;padding-right:60px;font-size:16px;display:inline-block;letter-spacing:normal;\"><span style=\"font-size: 16px; line-height: 1.2; word-break: break-word; mso-line-height-alt: 19px;\">CONFIRM EMAIL</span></span></a>\r\n"
				+ "																	<!--[if mso]></center></v:textbox></v:roundrect><![endif]-->\r\n"
				+ "																</div>\r\n"
				+ "															</td>\r\n"
				+ "														</tr>\r\n"
				+ "													</table>\r\n"
				+ "													<table class=\"image_block mobile_hide\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
				+ "														<tr>\r\n"
				+ "															<td style=\"padding-bottom:5px;padding-left:20px;padding-right:20px;padding-top:5px;width:100%;\">\r\n"
				+ "																<div align=\"center\" style=\"line-height:10px\"><img class=\"big\" src=\"https://d15k2d11r6t6rl.cloudfront.net/public/users/Integrators/BeeProAgency/820185_804124/digital%20wallet.png\" style=\"display: block; height: auto; border: 0; width: 560px; max-width: 100%;\" width=\"560\"></div>\r\n"
				+ "															</td>\r\n"
				+ "														</tr>\r\n"
				+ "													</table>\r\n"
				+ "													<div class=\"spacer_block\" style=\"height:10px;line-height:10px;font-size:1px;\">&#8202;</div>\r\n"
				+ "												</td>\r\n"
				+ "											</tr>\r\n"
				+ "										</tbody>\r\n"
				+ "									</table>\r\n"
				+ "								</td>\r\n"
				+ "							</tr>\r\n"
				+ "						</tbody>\r\n"
				+ "					</table>\r\n"
				+ "					<table class=\"row row-3\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-size: auto;\">\r\n"
				+ "						<tbody>\r\n"
				+ "							<tr>\r\n"
				+ "								<td>\r\n"
				+ "									<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-size: auto; color: #000000; width: 600px;\" width=\"600\">\r\n"
				+ "										<tbody>\r\n"
				+ "											<tr>\r\n"
				+ "												<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\r\n"
				+ "													<table class=\"heading_block\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
				+ "														<tr>\r\n"
				+ "															<td style=\"padding-bottom:5px;padding-top:25px;text-align:center;width:100%;\">\r\n"
				+ "																<h1 style=\"margin: 0; color: #6f6767; direction: ltr; font-family: 'Ubuntu', Tahoma, Verdana, Segoe, sans-serif; font-size: 36px; font-weight: 400; letter-spacing: normal; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0;\"><strong>You are one step away from unlimited access</strong></h1>\r\n"
				+ "															</td>\r\n"
				+ "														</tr>\r\n"
				+ "													</table>\r\n"
				+ "												</td>\r\n"
				+ "											</tr>\r\n"
				+ "										</tbody>\r\n"
				+ "									</table>\r\n"
				+ "								</td>\r\n"
				+ "							</tr>\r\n"
				+ "						</tbody>\r\n"
				+ "					</table>\r\n"
				+ "					<table class=\"row row-4\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
				+ "						<tbody>\r\n"
				+ "							<tr>\r\n"
				+ "								<td>\r\n"
				+ "									<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-position: center top; color: #000000; width: 600px;\" width=\"600\">\r\n"
				+ "										<tbody>\r\n"
				+ "											<tr>\r\n"
				+ "												<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-top: 0px; padding-bottom: 0px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\r\n"
				+ "													<table class=\"image_block\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
				+ "														<tr>\r\n"
				+ "															<td style=\"width:100%;padding-right:0px;padding-left:0px;\">\r\n"
				+ "																<div align=\"center\" style=\"line-height:10px\"><img class=\"fullMobileWidth\" src=\"https://d1oco4z2z1fhwp.cloudfront.net/templates/default/4011/bottom-rounded.png\" style=\"display: block; height: auto; border: 0; width: 600px; max-width: 100%;\" width=\"600\"></div>\r\n"
				+ "															</td>\r\n"
				+ "														</tr>\r\n"
				+ "													</table>\r\n"
				+ "												</td>\r\n"
				+ "											</tr>\r\n"
				+ "										</tbody>\r\n"
				+ "									</table>\r\n"
				+ "								</td>\r\n"
				+ "							</tr>\r\n"
				+ "						</tbody>\r\n"
				+ "					</table>\r\n"
				+ "				\r\n"
				+ "								</td>\r\n"
				+ "							</tr>\r\n"
				+ "						</tbody>\r\n"
				+ "					</table>\r\n"
				+ "				</td>\r\n"
				+ "			</tr>\r\n"
				+ "		</tbody>\r\n"
				+ "	</table><!-- End -->\r\n"
				+ "</body>\r\n"
				+ "\r\n"
				+ "</html>\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "";
	}
	
}
