package com.app.demo.service;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.demo.bean.Exam;
import com.app.demo.bean.Test;
import com.app.demo.bean.User;
import com.app.demo.bean.UserResponses;
import com.app.demo.bean.UserResponses.ResponseEntry;
import com.app.demo.dao.TestRepo;
import com.app.demo.dao.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService 
{
	@Autowired
	private UserRepo repo;
	
	@Autowired
	private TestRepo repo2;

	@Override
	public User authenticateUser(String username, String password) 
	{
		// TODO Auto-generated method stub
		return repo.findByUsernameAndPassword(username, password);
	}

	@Override
	public void registerUser(User user) 
	{
		// TODO Auto-generated method stub
		repo.save(user);
	}

	@Override
	public int getByEmail(String username, String email) 
	{
		// TODO Auto-generated method stub
		
		User forgotpassUser=repo.findByUsernameAndEmail(username, email);
		
		if(forgotpassUser!=null)
		{
			int otp=messageBody(forgotpassUser.getEmail(),forgotpassUser.getFirstname(),forgotpassUser.getLastname());
			
			return otp;
		}
		else
		{
			return 0;
		}
		
		
	}

	public boolean changePass(String username, String password)
	{
		User user1=repo.findByUsername(username);
		
		if(user1!=null)
		{
			user1.setPassword(password);
			repo.save(user1);
			return true;
		}
		
		return false;
	}

	@Override
	public User findUserProfile(String username) 
	{
		// TODO Auto-generated method stub
		User userProfile=repo.findByUsername(username);
		return userProfile;
	}
	
	//this is not correctly working
	@Transactional
	public void updateUserProfile(String username, User updatedProfile) 
	{
	    User userUpdatedProfile = repo.findByUsername(username);

	    if (userUpdatedProfile != null) 
	    {
	        // Update user profile fields
	        userUpdatedProfile.setFirstname(updatedProfile.getFirstname());
	        userUpdatedProfile.setLastname(updatedProfile.getLastname());
	        userUpdatedProfile.setEmail(updatedProfile.getEmail());
	        userUpdatedProfile.setMobileno(updatedProfile.getMobileno());
	        userUpdatedProfile.setQualification(updatedProfile.getQualification());
	        userUpdatedProfile.setDesignation(updatedProfile.getDesignation());

	        System.out.println("Updated Profile Before Database: " + userUpdatedProfile);
	        repo.save(userUpdatedProfile);
	    }
	}

	@Override
	public void calculatePoints(String username, Date startExamTimeDate, List<ResponseEntry> responses) 
	{
		Exam exam=new Exam();
		// TODO Auto-generated method stub
		for (ResponseEntry responseEntry : responses) 
		{
			if(responseEntry.getQuestionType().equals("positive"))
			{
				int selectedAnswer=0;
				if(responseEntry.getSelectedOption().equals("Strongly Agree"))
				{
					selectedAnswer=5;
				}
				else
				{
					selectedAnswer=Integer.parseInt(responseEntry.getSelectedOption());
				}
				
				switch(responseEntry.getPersonalityTrait())
				{
					case "Openness" :
					{
						exam.setOpenness(exam.getOpenness()+selectedAnswer);
						break;
					}
					case "Conscientiousness" :
					{
						exam.setConscientiousness(exam.getConscientiousness()+selectedAnswer);
						break;
					}
					case "Extraversion" :
					{
						exam.setExtraversion(exam.getExtraversion()+selectedAnswer);
						break;
					}
					case "Agreeableness" :
					{
						exam.setAgreeableness(exam.getAgreeableness()+selectedAnswer);
						break;
					}
					case "Neuroticism" :
					{
						exam.setNeuroticism(exam.getNeuroticism()+selectedAnswer);
						break;
					}
					default:
					{
						System.out.println("no personality traits");
					}
				}
			}
			else
			{
				int selectedAnswer=0;
				if(responseEntry.getSelectedOption().equals("Strongly Agree"))
				{
					selectedAnswer=1;
				}
				else
				{
					selectedAnswer=Integer.parseInt(responseEntry.getSelectedOption());
				}
				switch(responseEntry.getPersonalityTrait())
				{
					case "Openness" :
					{
						exam.setOpenness(exam.getOpenness()+selectedAnswer);
						break;
					}
					case "Conscientiousness" :
					{
						exam.setConscientiousness(exam.getConscientiousness()+selectedAnswer);
						break;
					}
					case "Extraversion" :
					{
						exam.setExtraversion(exam.getExtraversion()+selectedAnswer);
						break;
					}
					case "Agreeableness" :
					{
						exam.setAgreeableness(exam.getAgreeableness()+selectedAnswer);
						break;
					}
					case "Neuroticism" :
					{
						exam.setNeuroticism(exam.getNeuroticism()+selectedAnswer);
						break;
					}
					default:
					{
						System.out.println("no personality traits");
					}
				}
			}
			
		}
		
		
		//System.out.println("All Points :: "+exam.getOpenness()+"\t"+exam.getConscientiousness()+"\t"
		//+exam.getExtraversion()+"\t"+exam.getAgreeableness()+"\t"+exam.getNeuroticism());
		
		Test test=new Test(null, username, startExamTimeDate, exam.getOpenness(), exam.getConscientiousness(), 
				exam.getExtraversion(), exam.getAgreeableness(), exam.getNeuroticism());
		
		repo2.save(test);
	}
	
	
	
	
	@Override
	public List<Test> getTestsByUsername(String username) 
	{
		// TODO Auto-generated method stub
		return repo2.findByUsername(username);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	private static int messageBody(String recipient,String firstname,String lastname) {
	        System.out.println("Preparing to send message!..");

	        String subject = "Password Reset OTP - Big Five Personality Test";
	        String to = recipient;
	        String from = "arkhambatman08@gmail.com";

	        // Generate OTP
	        int otpLength = 6;
	        int generatedOTP = generateOTP(otpLength);
	        System.out.println("Generated OTP : "+generatedOTP);

	        // Create email body with OTP
	        String body = "Dear "+firstname+" "+lastname+",\n\n"
	                + "You have requested to reset your password for your 'Big Five Personality Test' account. Please use the following One-Time Password (OTP) to complete the password reset process:\n\n"
	                + "OTP: " + generatedOTP + "\n\n"
	                + "This OTP is valid for a limited time. Please do not share this OTP with anyone. If you did not initiate this password reset, please disregard this email.\n\n"
	                + "Thank you for using Big Five Personality Test.\n\n"
	                + "Best Regards,";

	        sendEmail(body, subject, to, from);
	        
	        return generatedOTP;
	    }
	
	 private static int generateOTP(int length) {
		    String allowedChars = "0123456789";
		    StringBuilder otp = new StringBuilder(length);
		    Random random = new Random();

		    for (int i = 0; i < length; i++) {
		        int index = random.nextInt(allowedChars.length());
		        otp.append(allowedChars.charAt(index));
		    }

		    // Convert the OTP string to an integer
		    return Integer.parseInt(otp.toString());
		}
	
	private static void sendEmail(String message, String subject, String to, String from) {
        // New Gmail host
        String host = "smtp.gmail.com";

        // System properties
        Properties properties = System.getProperties();
        System.out.println("Properties : " + properties);

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("arkhambatman08@gmail.com", "zpwy bunu fqfz pdmf");
            }
        });

        session.setDebug(true);

        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            Transport.send(mimeMessage);
            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

	


}
