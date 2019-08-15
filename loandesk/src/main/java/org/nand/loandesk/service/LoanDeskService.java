package org.nand.loandesk.service;


import com.fasterxml.jackson.databind.JsonNode;
import org.nand.loandesk.dao.LoanDeskDao;
import org.nand.loandesk.entities.Customer;
import org.nand.loandesk.entities.LoanApplication;
import org.nand.loandesk.vo.LoanApplicationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LoanDeskService {

    @Autowired
    private LoanDeskDao loanDeskDao;

    public Customer createCustomer(JsonNode customerNode){

        String phoneNum = customerNode.get("phoneNumber").asText();
        if(isCustomerExist(phoneNum)){
            return null;
        }else{
            Customer customer = new Customer();
            customer.setFullName(customerNode.get("fullName").asText());
            customer.setAddress(customerNode.get("address").asText());
            customer.setEmail(customerNode.get("email").asText());
            customer.setPhoneNumber(phoneNum);
            return loanDeskDao.createCustomer(customer);
        }
    }

    public Customer findCustomer(String phoneNum){
       return loanDeskDao.findCustomer(phoneNum);
    }

    public Long createLoanApplication(JsonNode loanApplicationRequest){
        LoanApplication application = new LoanApplication();

        String phoneNum = loanApplicationRequest.get("phoneNumber").asText();
        Customer customer = findCustomer(phoneNum);

        String emiStr = loanApplicationRequest.get("emi").asText();
        BigDecimal emi = new BigDecimal(emiStr);
        String salary = loanApplicationRequest.get("salary").asText();
        BigDecimal sal = new BigDecimal(salary);

        application.setCustomerId(customer.getId());
        application.setCompany(loanApplicationRequest.get("company").asText());
        application.setSalary(sal);
        application.setEmi(emi);

        LoanApplication newLoan = loanDeskDao.createLoanApplication(application);
        return newLoan.getId();
    }

    public List<LoanApplication> getLoanApplications(Customer customer){

        return loanDeskDao.getLoanApplicationsByCustomer(customer.getId());
    }

    public boolean isCustomerExist(String phoneNum){
        Customer existingCustomer = findCustomer(phoneNum);
        if(existingCustomer!=null){
            return true;
        }
        return false;
    }
}
