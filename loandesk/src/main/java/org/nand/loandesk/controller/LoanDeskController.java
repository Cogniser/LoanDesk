package org.nand.loandesk.controller;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.nand.loandesk.entities.Customer;
import org.nand.loandesk.entities.LoanApplication;
import org.nand.loandesk.service.LoanDeskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("loandesk")
public class LoanDeskController {

    @Autowired
    private LoanDeskService loanDeskService;

    @GetMapping("/health")
    public String health(){
        return "Hi,I am loan desk app.i am doing good.";
    }

    @PostMapping("/loan")
    public ResponseEntity createLoan(@RequestBody String req){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode loanNode = objectMapper.readTree(req);
            Long loanId = loanDeskService.createLoanApplication(loanNode);
            //String fileName = "Loan_"+new Date()+".json";
            //objectMapper.writeValue(new File("/home/nandeesh/Desktop/Loan_Desk/"+fileName),loanNode);

            return ResponseEntity.ok(loanId);

        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/loan/{phoneNumber}")
    public ResponseEntity getLoanDetails(@PathVariable(value = "phoneNumber") String phoneNumber){
        List<LoanApplication> loanList;
        try{
            Customer customer = loanDeskService.findCustomer(phoneNumber);
            if(customer!=null){
                loanList = loanDeskService.getLoanApplications(customer);
                ObjectMapper objectMapper = new ObjectMapper();

                String loansStr = objectMapper.writeValueAsString(loanList);
                String customerStr = objectMapper.writeValueAsString(customer);

                ObjectNode loanDetails = objectMapper.createObjectNode();
                loanDetails.put("customer",customerStr);
                loanDetails.put("loans",loansStr);
                return ResponseEntity.ok(loanDetails);

                //return ResponseEntity.ok(loanList);
            }else{
                return ResponseEntity.badRequest()
                        .body("Customer details not found with phone number"+phoneNumber);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/customer")
    public ResponseEntity createCustomer(@RequestBody String req){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode customerNode = objectMapper.readTree(req);
            Customer customer = loanDeskService.createCustomer(customerNode);
            if(customer!=null){
                URI uri = new URI("/customer/"+customer.getPhoneNumber());
                return ResponseEntity.created(uri).build();
            }
            else{
                String phoneNum = customerNode.get("phoneNumber").asText();
                return ResponseEntity.badRequest().body("Customer already exist with "+phoneNum);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("customer/{phoneNum}")
    public ResponseEntity getCustomer(@PathVariable("phoneNum") String phoneNum){
        try{
            Customer customer = loanDeskService.findCustomer(phoneNum);
            if(customer!=null)
                return ResponseEntity.ok(customer);
            else
                return ResponseEntity.badRequest()
                        .body("Customer details not found with phone number"+phoneNum);
        }catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Exception while getting customer details with phone number "+phoneNum);
        }

    }
}
