package com.smart.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import com.smart.Dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String home(Model m) {
		m.addAttribute("title", "Home -Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "About -Smart Contact Manager");
		return "about";
	}

	@GetMapping("/Signup")
	public String signup(Model m) {
		m.addAttribute("title", "Register -Smart Contact Manager");
		m.addAttribute("user", new User());
		return "signup";
	}

/////////////////////// register page////////////
	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1,@RequestParam(value = "agreement", defaultValue = "false") boolean agreement,Model model,
      HttpSession session) {
       
		
		try {
            if (!agreement) {
                System.out.println("User did not agree to the terms and conditions");
                throw new Exception("User did not agree to the terms and conditions");
            }
            
            
            if(result1.hasErrors()) {
            	
            	System.out.println("Error"+result1.toString());
            	model.addAttribute("user",user);
            	return "signup";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            
           
            userRepository.save(user);
            model.addAttribute("user", new User());
            session.setAttribute("message", new message("Registration Successful!!", "alert-success"));
            return "signup";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new message("Something went wrong: " + e.getMessage(), "alert-danger"));

            return "signup";
        }
    }
	
	@RequestMapping("/signup")
	public String removeSessionAttribute(HttpSession session,Model m) {
	    session.removeAttribute("message");
	    
	    m.addAttribute("user",new User());
	    return "signup"; 
	}
/////////////////////register page work end///////////////////
	@RequestMapping("/logic")
	public String loginpage(Model m){
		m.addAttribute("title", "Login page");
		return "login";
	}
	
	

}
