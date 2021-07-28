package com.lucasrodrigues.bankapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasrodrigues.bankapi.dto.AccountDTO;
import com.lucasrodrigues.bankapi.dto.BalanceDTO;
import com.lucasrodrigues.bankapi.dto.TransferDTO;
import com.lucasrodrigues.bankapi.dto.UserDTO;
import com.lucasrodrigues.bankapi.exception.*;
import com.lucasrodrigues.bankapi.model.Account;
import com.lucasrodrigues.bankapi.model.Balance;
import com.lucasrodrigues.bankapi.model.Transfer;
import com.lucasrodrigues.bankapi.model.User;
import com.lucasrodrigues.bankapi.service.AccountService;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class BankApiApplicationTests {

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
    private AccountService accountService;
		
	private Map<?,?> getAuthUser() throws JsonProcessingException, Exception {
		User user = new User("email@test.com", "test123", "Test User");
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth")
                .content(objectMapper.writeValueAsString(new ObjectMapper().readValue("{\"email\": \"email@test.com\", \"password\": \"test123\"}", Map.class)))
                .contentType(MediaType.APPLICATION_JSON))
        		.andReturn();
		return new ObjectMapper().readValue(result.getResponse().getContentAsString(), Map.class);
	}
	
	@Test
	public void shouldCreateNewUser() throws Exception {
		User user = new User("test@test.com", "test123", "Test User");
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
		        .andExpect(status().isCreated())
				.andExpect(jsonPath("$.email").value("test@test.com"));
	}
	
	@Test
	public void shouldNotCreateTwoUsersWithSameEmail() throws Exception {
		User user = new User("same@test.com", "test123", "Test User");
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
		        .andExpect(status().isCreated())
				.andExpect(jsonPath("$.email").value("same@test.com"));
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
		        .andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void shouldNotCreateUserWithEmptyFields() throws Exception {
		User user = new User("test@test.com", "", "Test User");
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
		        .andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void shouldNotCreateUserWithWrongEmailFormat() throws Exception {
		User user = new User("testtest.com", "test123", "Test User");
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
		        .andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void shouldNotCreateUserWithSmallPassword() throws Exception {
		User user = new User("testl@test.com", "test", "Test User");
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
		        .andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void shouldAuthUser() throws Exception {
		User user = new User("auth@test.com", "test123", "Test User");
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders.post("/auth")
                .content(objectMapper.writeValueAsString(new ObjectMapper().readValue("{\"email\": \"auth@test.com\", \"password\": \"test123\"}", Map.class)))
                .contentType(MediaType.APPLICATION_JSON))
		        .andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("auth@test.com"));
	}
	
	@Test
	public void shouldNotAuthInexistentUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth")
                .content(objectMapper.writeValueAsString(new ObjectMapper().readValue("{\"email\": \"notauth@test.com\", \"password\": \"test123\"}", Map.class)))
                .contentType(MediaType.APPLICATION_JSON))
		        .andExpect(status().isUnauthorized());
	}
	
	@Test
	public void shouldNotAuthWithEmptyFields() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth")
                .content(objectMapper.writeValueAsString(new ObjectMapper().readValue("{\"email\": \"notauth@test.com\", \"password\": \"\"}", Map.class)))
                .contentType(MediaType.APPLICATION_JSON))
		        .andExpect(status().isUnauthorized());
	}
	
	@Test
	public void shouldCreateAccount() throws Exception {
        Account account = new Account(getAuthUser().get("email").toString(), "1000-1", 250);
        when(accountService.save(any(Account.class))).thenReturn(new AccountDTO(account.getNumber(), account.getBalance(), new UserDTO(getAuthUser().get("email").toString(), getAuthUser().get("name").toString())));
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
        		.andExpect(jsonPath("$.number").value("1000-1"));
    }
	
	@Test
	public void shouldNotCreateAccountWithoutToken() throws Exception {
        Account account = new Account(getAuthUser().get("email").toString(), "1000-5", 250);
        when(accountService.save(any(Account.class))).thenThrow(InternalAuthenticationServiceException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
	
	@Test
	public void shouldNotCreateAccountWithNegativeBalance() throws Exception {
        Account account = new Account(getAuthUser().get("email").toString(), "1000-5", -250);
        when(accountService.save(any(Account.class))).thenThrow(NegativeBalanceException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
	
	@Test
	public void shouldNotCreateTwoAccountsWithSameNumber() throws Exception {
		Map<?, ?> user = getAuthUser();
        Account account = new Account(user.get("email").toString(), "1000-1", 250);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + user.get("token").toString())
        		.content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        when(accountService.save(any(Account.class))).thenThrow(AlreadyRegisteredAccountNumberException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + user.get("token").toString())
        		.content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
	
	@Test
	public void shouldMakeTransfer() throws Exception {
        Account account = new Account(getAuthUser().get("email").toString(), "1001-1", 250);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON));
        Account account2 = new Account(getAuthUser().get("email").toString(), "1001-2", 0);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account2))
                .contentType(MediaType.APPLICATION_JSON));
        Transfer transfer = new Transfer("1001-1", "1001-2", 100);
        when(accountService.transfer(any(Transfer.class))).thenReturn(new TransferDTO(transfer.getAmount(), transfer.getSource_account_number(), transfer.getDestination_account_number(), new UserDTO(getAuthUser().get("email").toString(), getAuthUser().get("name").toString())));
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(transfer))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        		.andExpect(jsonPath("$.amount").value("100.0"));
    }
	
	@Test
	public void shouldNotMakeTransferWithoutToken() throws Exception {
        Account account = new Account("notowner@test.com", "1001-1", 250);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON));
        Account account2 = new Account(getAuthUser().get("email").toString(), "1001-2", 0);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account2))
                .contentType(MediaType.APPLICATION_JSON));
        Transfer transfer = new Transfer("1001-1", "1001-2", 100);
        when(accountService.transfer(any(Transfer.class))).thenThrow(InternalAuthenticationServiceException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
                .content(objectMapper.writeValueAsString(transfer))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
	
	@Test
	public void shouldNotMakeTransferIfUserIsNotOwnerOfAccount() throws Exception {
        Account account = new Account("notowner@test.com", "1001-1", 250);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON));
        Account account2 = new Account(getAuthUser().get("email").toString(), "1001-2", 0);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account2))
                .contentType(MediaType.APPLICATION_JSON));
        Transfer transfer = new Transfer("1001-1", "1001-2", 100);
        when(accountService.transfer(any(Transfer.class))).thenThrow(UserNotOwnerOfAccountException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(transfer))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
	
	@Test
	public void shouldNotMakeTransferIfSourceAndDestinationAccountAreTheSame() throws Exception {
        Account account = new Account(getAuthUser().get("email").toString(), "1001-1", 250);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON));
        Transfer transfer = new Transfer("1001-1", "1001-1", 100);
        when(accountService.transfer(any(Transfer.class))).thenThrow(SameAccountException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(transfer))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
	
	@Test
	public void shouldNotMakeTransferIfSourceAccountDoesntExists() throws Exception {
        Account account = new Account(getAuthUser().get("email").toString(), "1001-2", 100);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON));
        Transfer transfer = new Transfer("1001-1", "1001-2", 100);
        when(accountService.transfer(any(Transfer.class))).thenThrow(SourceAccountNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(transfer))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
	
	@Test
	public void shouldNotMakeTransferIfDestinationAccountDoesntExists() throws Exception {
        Account account = new Account(getAuthUser().get("email").toString(), "1001-1", 100);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON));
        Transfer transfer = new Transfer("1001-1", "1001-2", 100);
        when(accountService.transfer(any(Transfer.class))).thenThrow(DestinationAccountNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(transfer))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
	
	@Test
	public void shouldNotMakeTransferIfSourceBalanceIsInsufficient() throws Exception {
        Account account = new Account(getAuthUser().get("email").toString(), "1001-1", 0);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON));
        Account account2 = new Account(getAuthUser().get("email").toString(), "1001-2", 250);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account2))
                .contentType(MediaType.APPLICATION_JSON));
        Transfer transfer = new Transfer("1001-1", "1001-2", 100);
        when(accountService.transfer(any(Transfer.class))).thenThrow(InsufficientBalanceException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(transfer))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
	
	@Test
	public void shouldNotMakeTransferIfAmountIsZeroOrNegative() throws Exception {
        Account account = new Account(getAuthUser().get("email").toString(), "1001-1", 0);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON));
        Account account2 = new Account(getAuthUser().get("email").toString(), "1001-2", 250);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account2))
                .contentType(MediaType.APPLICATION_JSON));
        Transfer transfer = new Transfer("1001-1", "1001-2", 0);
        when(accountService.transfer(any(Transfer.class))).thenThrow(NegativeBalanceException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/transfer")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(transfer))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
	
	@Test
	public void shouldGetBalance() throws Exception {
        Account account = new Account(getAuthUser().get("email").toString(), "1001-1", 250);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON));
        Balance balance = new Balance(account.getNumber());
        when(accountService.balance(any(Balance.class))).thenReturn(new BalanceDTO(account.getNumber(), account.getBalance()));
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/balance")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        		.andExpect(jsonPath("$.balance").value("250.0"));
    }
	
	@Test
	public void shouldNotGetBalanceWithoutToken() throws Exception {
        Account account = new Account(getAuthUser().get("email").toString(), "1001-1", 250);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON));
        Balance balance = new Balance(account.getNumber());
        when(accountService.balance(any(Balance.class))).thenThrow(InternalAuthenticationServiceException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/balance")
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
	
	@Test
	public void shouldNotGetBalanceIfUserIsNotTheOwnerOfAccount() throws Exception {
        Account account = new Account("notowner@test.com", "1001-1", 250);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON));
        Balance balance = new Balance(account.getNumber());
        when(accountService.balance(any(Balance.class))).thenThrow(UserNotOwnerOfAccountException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/balance")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
	
	@Test
	public void shouldNotGetBalanceIfAccountDoesntExist() throws Exception {
        Balance balance = new Balance("0000-0");
        when(accountService.balance(any(Balance.class))).thenThrow(AccountNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/balance")
        		.header("Authorization", "Bearer " + getAuthUser().get("token").toString())
                .content(objectMapper.writeValueAsString(balance))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
	
}
