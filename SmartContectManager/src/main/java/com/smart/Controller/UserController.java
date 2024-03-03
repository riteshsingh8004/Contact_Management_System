 package com.smart.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.Dao.ContactRepository;
import com.smart.Dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	////////method to adding common data
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal ){
		
		String userName = principal.getName();
		
		User user = userRepository.getUserByName(userName);
		
		
		model.addAttribute("user", user);
	}
	
	//////////dashboard home
	
	@RequestMapping("/index")
	public String dashboard(Model model ,Principal principal)
	{ 
		model.addAttribute("title","User dashboard");
		return "normal/user_dashboard";
	}
	
	/////////////open add contact
	
	@RequestMapping("/add-contact")
	public String openAddContactFrom(Model model,HttpSession session){
		
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		
		session.removeAttribute("message");
		
		return "normal/add_contact_from";
	}
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file,Principal principal,Model model,HttpSession session) {
		
		try {
		String name = principal.getName();
		User user = this.userRepository.getUserByName(name);
		contact.setUser(user);
		System.out.println("ritesh=====>"+contact);
		
		////////processing and uploading  file...
		
		if(file.isEmpty()) {
			//if the file is empty
			System.out.println("image is empty");
			contact.setImage("contact.png");
		}else {
			///upload the file to folder and update the name to contact
		    contact.setImage(file.getOriginalFilename());
		    File saveFile = new ClassPathResource("static/image").getFile();
		    Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		    
		    Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
		    System.out.println("Image is uploaded");
		}
		
		user.getContacts().add(contact);
		
		this.userRepository.save(user);
		System.out.println("contact=======>"+contact);
		System.out.println("Added the Data base");	
		model.addAttribute("title","Add Contact");
		 ///meggage error
	    session.setAttribute("message",new message("Your contact is added !! Add more...","success"));
		}catch (Exception e) {
			System.out.println("ERROR"+e.getMessage());
			e.printStackTrace();
			 ///meggage error
		    session.setAttribute("message",new message("Something went wrong !! Try again","danger"));
		}
		return "normal/add_contact_from";
	}
	
	///////////////show contacts
	///current page = page;
	//number of contanct in 1 page =5;
	@RequestMapping("/show-contact/{page}")
	public String showContact(@PathVariable("page") Integer page,Model model,Principal principal) {
		model.addAttribute("title","Show Contact");
	    String userName = principal.getName(); 
	    User user = this.userRepository.getUserByName(userName);
	    
	    Pageable pageable = PageRequest.of(page,7);
	    
	    Page<Contact> contacts = this.contactRepository.findContactByuser(user.getUid(),pageable);
		
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_added_contacts";
	}
	
	@RequestMapping("/{cId}/contact")
	public String showingSpecificDetail(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		
		System.out.println("cID===>"+cId);
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
		String userName = principal.getName();
		
		User user = this.userRepository.getUserByName(userName);
		
		System.out.println("ritesh====>"+user);
		
		if(user.getUid() == contact.getUser().getUid()) {
			
			model.addAttribute("contact", contact);
			model.addAttribute("title",contact.getName());
		}
		
		return "/normal/contact_details";
	}
	
	@GetMapping("/delete/{cId}")
	public String delectcontactFromLoginUser(@PathVariable("cId") Integer cid,Model model,Principal principal,HttpSession session) {
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		//contact.setUser(null);
		String username = principal.getName();
	        User user = this.userRepository.getUserByName(username);
	        
	        if(user.getUid() == contact.getUser().getUid()) {
	        	this.contactRepository.delete(contact);
	        	session.setAttribute("messages",new message("Contact Deleted Successfully....!", "success"));	
	        }
	        
		
		return"redirect:/user/show-contact/0";
	}
	
	@PostMapping("/update-contact/{cId}")
	public String updateContact(@PathVariable("cId") Integer cId,Model model) {
		
		
		Optional<Contact> contactoptional = this.contactRepository.findById(cId);
		Contact contact = contactoptional.get();
		model.addAttribute("title","Updata Contacts");
		model.addAttribute("contact",contact);
		return "/normal/edit_Contact";
	}
	
	/* @PostMapping("/process-update") */
	@RequestMapping(value= "/process-update" , method=RequestMethod.POST)
	public String updateContactDetails(@ModelAttribute Contact contact,Principal principal,@RequestParam("profileImage")MultipartFile file ,Model model,HttpSession session) {
		
		try {
		//old contect detail fetch firsht
		Contact oldContactDetils = this.contactRepository.findById(contact.getcId()).get();
		System.out.println("ritesh===>"+oldContactDetils.getName());
		System.out.println("ritesh===>"+oldContactDetils.getImage());

		if(!file.isEmpty()) {
			///delete old file
			 File deleteFile = new ClassPathResource("static/image").getFile();
			 File file1= new File(deleteFile, oldContactDetils.getImage());
		     file1.delete();
			 //upload new file
			
			 File saveFile = new ClassPathResource("static/image").getFile();
			 Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			 Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			 contact.setImage(file.getOriginalFilename());
			
		}else {
			contact.setImage(oldContactDetils.getImage());
		}
		
       // String userName = principal.getName();
        //User  user = this.userRepository.getUserByName(userName);
		
		 User user = this.userRepository.getUserByName(principal.getName());
		 contact.setUser(user);
		 
	    
	    System.out.println("ritesh===>"+contact.getName());
        this.contactRepository.save(contact);
		
        session.setAttribute("message", new message("Your contact is updated", "success"));
        
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return"redirect:/user/"+contact.getcId()+"/contact";
	}
	
	@GetMapping("/profile")
	public String userprofile(Model model)
	{
		
		model.addAttribute("title","User profile");
		return "normal/profile";
	}
	
	@PostMapping("/update-user/{uid}")
	public String editUserDetail(@PathVariable("uid") Integer uid,Model model,Principal principal) {
		
		/*
		 * String userName = principal.getName();
		 * 
		 * int user = this.userRepository.getUserByName(userName).getUid();
		 */
		
		Optional<User> userOptional = this.userRepository.findById(uid);
		User user = userOptional.get();
		model.addAttribute("title","update user");
		model.addAttribute("user",user);
		
		return "normal/edit_user_detail";
	}
	
	@PostMapping("/update_detail")
	public String processEditUserDetail(@ModelAttribute User user,Model model,@RequestParam("profileImage") MultipartFile file,@RequestParam(value = "agreement", defaultValue = "false") boolean agreement,Principal principal,HttpSession session) {
		
		try {
			//first fetch old detail
			 User user2 = this.userRepository.findById(user.getUid()).get();
			 
			 System.out.println("abhay1=====>"+user2.getName());
			 System.out.println("abhay2=====>"+user2.getEmail());
			 System.out.println("abhay3=====>"+user2.getImageUrl());
			 System.out.println("abhay4=====>"+user2.getRole());
			 System.out.println("abhay5=====>"+user2.getAbout());
			 
			 if(!agreement) {
				 System.out.println("User did not agree to the terms and conditions");
	                throw new Exception("User did not agree to the terms and conditions");
			 }
			 
			 if(!file.isEmpty()) {
					/* delete old file */
				 File deleteFile = new ClassPathResource("static/image").getFile();
				 File file1= new File(deleteFile, user2.getImageUrl());
			     file1.delete();
			     
			     
			     //save file
			     File saveFile = new ClassPathResource("static/image").getFile();
				 Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				 Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				// contact.setImage(file.getOriginalFilename());
				 user.setImageUrl(file.getOriginalFilename());
			     
			 }else {
				 user.setImageUrl(user2.getImageUrl());
			 }
		   this.userRepository.save(user2);
		   model.addAttribute("user", new User());
		   session.setAttribute("message2",new message("Your Contact is updated sucessfully..!!","success"));
			
			return"redirect:/user/profile";
		}catch(Exception e){
			e.printStackTrace();	
			model.addAttribute("user", user);
            session.setAttribute("message2", new message("Something went wrong: " + e.getMessage(), "alert-danger"));

    		return"redirect:/user/profile";
		}
		
	}
	 @PostMapping("/removeSessionMessage")
	    public void removeSessionMessage(HttpSession session) {
	        // Remove the session attribute
	        session.removeAttribute("message2");
	    }
	
}
