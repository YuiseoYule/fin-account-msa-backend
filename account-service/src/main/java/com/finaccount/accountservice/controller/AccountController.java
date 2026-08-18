package com.finaccount.accountservice.controller;

import com.finaccount.accountservice.vo.AccountRequest;
import com.finaccount.accountservice.dto.AccountDto;
import com.finaccount.accountservice.service.AccountService;
import com.finaccount.accountservice.vo.AccountResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
public class AccountController {
    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping("/accounts")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest request) {
        AccountDto dto = new AccountDto();
        dto.setOwnerName(request.getOwnerName());
        dto.setPassword(request.getPassword());

        AccountDto created = service.createAccount(dto);
        AccountResponse response = dtoToResponse(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Integer accountId) {
        try {
            AccountDto dto = service.getAccountByAccountId(accountId);
            AccountResponse response = dtoToResponse(dto);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/accounts/{accountId}")
    public ResponseEntity<AccountResponse> updateAccount(
            @PathVariable Integer accountId,
            @RequestBody AccountRequest request
    ) {
        try {
            AccountDto dto = new AccountDto();
            dto.setBalance(request.getBalance());
            dto.setStatus(request.getStatus());

            AccountDto updated = service.updateAccount(accountId, dto);
            AccountResponse response = dtoToResponse(updated);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private AccountResponse dtoToResponse(AccountDto dto) {
        AccountResponse response = new AccountResponse();
        response.setAccountId(dto.getAccountId());
        response.setAccountNumber(dto.getAccountNumber());
        response.setOwnerName(dto.getOwnerName());
        response.setBalance(dto.getBalance());
        response.setStatus(dto.getStatus());
        return response;
    }
}