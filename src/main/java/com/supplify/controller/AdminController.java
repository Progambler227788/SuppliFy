package com.supplify.controller;

import com.supplify.entity.Seller;
import com.supplify.services.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;import com.supplify.entity.Seller;
import com.supplify.services.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import com.supplify.entity.Seller;
import com.supplify.services.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SellerService sellerService;

    @GetMapping("/all-sellers")
    public String getAllSellers(Model model) {
        List<Seller> allSellers = sellerService.getAllSellers(); // This method should return all sellers
        model.addAttribute("allSellers", allSellers);
        return "all-sellers"; // This is the name of your Thymeleaf template (e.g., all-sellers.html)
    }
    @GetMapping("/rejected-sellers")
    public String getrejectedSellers(Model model){
        List<Seller> rejectedSellers = sellerService.getRejectedSellers();
        model.addAttribute("rejectedSellers", rejectedSellers);
        return "rejected-seller";

    }

    @GetMapping("/pending-sellers")
    public String getPendingSellers(Model model) {
        List<Seller> pendingSellers = sellerService.getPendingSellers();
        List<Seller> rejectedSellers = sellerService.getRejectedSellers();
        model.addAttribute("pendingSellers", pendingSellers); // Add pending sellers to the model
        model.addAttribute("rejectedSellers", rejectedSellers); // Add rejected sellers to the model
        return "admin"; // Return the name of the Thymeleaf template (admin.html)
    }

    @PostMapping("/approve-seller/{id}")
    public String approveSeller(@PathVariable UUID id) {
        sellerService.approveSeller(id); // Implement this method in your service
        return "redirect:/admin/pending-sellers"; // Redirect back to the pending sellers page
    }

    @PostMapping("/reject-seller/{id}")
    public String rejectSeller(@PathVariable UUID id) {
        sellerService.rejectSeller(id);
        return "redirect:/admin/pending-sellers";
        //ResponseEntity.ok("Seller rejected successfully");
    }
}/*
package com.supplify.controller;

import com.supplify.entity.Seller;
import com.supplify.services.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*") // Consider restricting this in production
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SellerService sellerService;

    */
/*@GetMapping("/pending-sellers")
    public ResponseEntity<List<Seller>> getPendingSellers() {
        List<Seller> pendingSellers = sellerService.getPendingSellers();

        return ResponseEntity.ok(pendingSellers);
    }*//*

    @GetMapping("/pending-sellers")
    public String getPendingSellers(Model model) {
        List<Seller> pendingSellers = sellerService.getPendingSellers();
        model.addAttribute("sellers", pendingSellers); // Add sellers to the model
        return "admin"; // Return the name of the Thymeleaf template (admin.html)
    }

    @PostMapping("/approve-seller/{id}")
    public ResponseEntity<String> approveSeller(@PathVariable UUID id) {
        Seller seller = sellerService.findById(id);
        if (seller != null) {
            seller.setApproved(true);
            sellerService.save(seller);
            return ResponseEntity.ok("Seller approved successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reject-seller/{id}")
    public ResponseEntity<String> rejectSeller(@PathVariable UUID id) {
        sellerService.rejectSeller(id);
        return ResponseEntity.ok("Seller rejected successfully");
    }
}*/
