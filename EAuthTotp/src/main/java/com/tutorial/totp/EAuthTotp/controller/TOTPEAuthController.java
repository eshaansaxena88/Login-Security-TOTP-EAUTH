package com.tutorial.totp.EAuthTotp.controller;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.taimos.totp.TOTP;

@Controller
public class TOTPEAuthController {
    
	@Value("${file.SecretKey}")
	private String SecretKey;
	
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam("username") String username, 
                        @RequestParam("password") String password) {
      // Simply byspassing UserAuthentication.
        return "redirect:/totp";
    }
    
    @GetMapping("/totp")
    public String totpPage(Model model) {
        // Generate the TOTP URI for the user   
        String otpUri = generateOtpUri();
        
        // Add the OTP URI to the model for rendering in the template
        model.addAttribute("otpUri", otpUri);
        
        return "totp";
    }
    
    @PostMapping("/totp")
    public String verifyTotp(@RequestParam("verificationCode") String verificationCode) {
        // Validate the TOTP verification code // TO DO inside
        boolean isCodeValid = validateVerificationCode(verificationCode); // Replace with your TOTP validation logic
        
        if (isCodeValid) {
            // TOTP verification successful, proceed with login or other actions
            return "redirect:/success";
        } else {
            // TOTP verification failed, redirect back to TOTP page
            return "redirect:/totp?error";
        }
    }
    
    @GetMapping("/success")
    public String successPage() {
        // Render the success page after successful login and TOTP verification
        return "success";
    }
    
    // Helper methods for TOTP generation and validation
    
    private String generateOtpUri() {
    	 
 	    String secretKey = SecretKey;//base32.encodeToString(bytes);
 		
 	    
 	   String userEmail = "eshaansaxena88@gmail.com";
 	  String companyName = "Self Tutorial";
 	  String barCodeUrl = getGoogleAuthenticatorBarCode(secretKey, userEmail, companyName);
    	
        return barCodeUrl;
    }
    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
	    try {
	        return "otpauth://totp/"
	                + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
	                + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
	                + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
	    } catch (UnsupportedEncodingException e) {
	        throw new IllegalStateException(e);
	    }
	}
 
 
    private boolean validateVerificationCode(String verificationCode) {
    	  Base32 base32 = new Base32();
		    byte[] bytes = base32.decode(SecretKey);
		    String hexKey = Hex.encodeHexString(bytes);
		    return verificationCode.equals(TOTP.getOTP(hexKey));
    }
}