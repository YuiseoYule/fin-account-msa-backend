package com.finaccount.accountservice.service;

import com.finaccount.accountservice.dto.AccountStatus;
import com.finaccount.accountservice.dto.AccountDto;
import com.finaccount.accountservice.jpa.AccountEntity;
import com.finaccount.accountservice.jpa.AccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AccountService implements UserDetailsService {
    private final AccountRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AccountService(AccountRepository repository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountDto createAccount(AccountDto dto) {
        AccountEntity entity = new AccountEntity();
        entity.setOwnerName(dto.getOwnerName());
        entity.setAccountNumber(new AccountNumberGenerator().generate());
        entity.setBalance(0L);
        entity.setStatus(AccountStatus.ACTIVE);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        entity = repository.save(entity);

        return entityToDto(entity);
    }

    public AccountDto getAccountByAccountId(Integer accountId) throws NoSuchElementException {
        AccountEntity entity = repository.findById(accountId).orElseThrow();
        return entityToDto(entity);
    }

    public AccountDto getAccountByAccountNumber(String accountNumber) throws NoSuchElementException {
        AccountEntity entity = repository.findByAccountNumber(accountNumber).orElseThrow();
        return entityToDto(entity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            AccountDto dto = getAccountByAccountNumber(username);

            return new User(
                    dto.getAccountNumber(),
                    dto.getPassword(),
                    true,
                    true,
                    true,
                    true,
                    List.of()
            );

        } catch (NoSuchElementException e) {
            throw new UsernameNotFoundException(username);
        }
    }

    public AccountDto updateAccount(Integer accountId, AccountDto dto) throws NoSuchElementException {
        AccountEntity entity = repository.findById(accountId).orElseThrow();

        if (isBalanceToBeUpdated(dto)) {
            entity.setBalance(dto.getBalance());
        }

        if (isStatusToBeUpdated(dto)) {
            entity.setStatus(dto.getStatus());
        }

        AccountEntity saved = repository.save(entity);
        return entityToDto(saved);
    }

    private boolean isBalanceToBeUpdated(AccountDto dto) {
        return dto.getBalance() != null;
    }

    private boolean isStatusToBeUpdated(AccountDto dto) {
        return dto.getStatus() != null;
    }

    private AccountDto entityToDto(AccountEntity entity) {
        AccountDto dto = new AccountDto();
        dto.setAccountId(entity.getAccountId());
        dto.setAccountNumber(entity.getAccountNumber());
        dto.setOwnerName(entity.getOwnerName());
        dto.setPassword(entity.getPassword());
        dto.setBalance(entity.getBalance());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
