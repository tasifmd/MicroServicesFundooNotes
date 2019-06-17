package com.bridgelabz.user.service;

import java.util.List;

import com.bridgelabz.response.LoginResponse;
import com.bridgelabz.response.Response;
import com.bridgelabz.user.dto.ForgotDto;
import com.bridgelabz.user.dto.LoginDto;
import com.bridgelabz.user.dto.RegisterDto;
import com.bridgelabz.user.dto.ResetDto;
import com.bridgelabz.user.model.User;

/**
 * Purpose : Creating IUserServices interface to add services to FundooBackend
 * @author : Tasif Moahmmed
 *
 */
public interface IUserService {
	
	/**
	 * Purpose : Method to get all user
	 * @return
	 */
	public List<User> getAllUser();
	/**
	 * Purpose : Method for register
	 * @param registerDto
	 * @return
	 */
	public Response register(RegisterDto registerDto);

	/**
	 * Purpose : Method to validate email
	 * @param token
	 * @return
	 */
	public Response validateEmail(String token);
	
	/**
	 * Purpose : Method fror login
	 * @param loginDto
	 * @return
	 */
	public LoginResponse login(LoginDto loginDto);
	
	/**
	 * Purpose : Method for forgot password
	 * @param forgotDto
	 * @return
	 */
	public Response forgotPassword(ForgotDto forgotDto);

	/**
	 * Purpose : Method for reset password
	 * @param resetDto
	 * @param token
	 * @return
	 */
	public Response resetPassword(ResetDto resetDto,String token);
}
