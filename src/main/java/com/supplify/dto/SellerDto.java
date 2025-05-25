package com.supplify.dto;


import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerDto
{
    private Long id;
    
    @NotBlank(message = "Name is empty")
    private String firstName;
    
    @NotBlank(message = "Last Name is empty")
    private String lastName;
    
    @NotBlank(message = "Email should not be empty")
    @Email
    private String email;
    
    @NotBlank(message = "Password should not be empty")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$", 
             message = "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, a number, and a special character.")
    private String password;
    
    @NotBlank(message = "Confirm Password should not be empty")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$", 
             message = "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, a number, and a special character.")
    private String confirmpassword;
    
    @NotBlank(message = "Phone number is empty")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;

    private  MultipartFile[] images;

    @NotBlank(message = "Shop name cannot be empty")
    @Size(min = 3, max = 50, message = "Shop name must be between 3 and 50 characters")
    private String shopName;

   

    @NotBlank(message = "Address cannot be empty")
    @Size(min = 5,  max = 100, message = "Address should be between 5 and 100 characters")
    private String address;

    

    @NotBlank(message = "Zipcode cannot be empty")
    @Pattern(regexp = "^[0-9]{5,6}$", message = "Zipcode should be 5 or 6 digits long")
    private String zipcode;

   
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmpassword);
    }


}