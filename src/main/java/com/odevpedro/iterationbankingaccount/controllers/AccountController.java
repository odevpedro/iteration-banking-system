package com.odevpedro.iterationbankingaccount.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/v1/contas")

public class AccountController {
    @Autowired
    AccountService accountService;

    @PostMapping()
    @ApiOperation(value = "Cadastra uma nova conta")
    public ResponseEntity<Object> save(@RequestBody @Valid AccountDto accountDto) {
        if (!Status.ATIVO.name().equals(accountDto.getActiveAccountIndicator().toUpperCase())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Indicador de conta ativa diferente de ATIVO");
        }
        if (!TypeAccount.CORRENTE.name().equals(accountDto.getAccountTypeName().toUpperCase()) &&
                !TypeAccount.POUPANCA.name().equals(accountDto.getAccountTypeName().toUpperCase())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tipo de conta diferente de CORRENTE ou POUPANCA");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.save(accountDto));
    }

    @PatchMapping("/{id}/saque")
    @ApiOperation(value = "Efetua um saque em uma conta")
    public ResponseEntity<Object> withdrawalMoney(@RequestBody @Valid WithdrawalAccountDTO withdrawalAccountDTO, @PathVariable(value = "id") UUID id) {
        accountService.withdrawalMoney(id, withdrawalAccountDTO);
        return ResponseEntity.ok("Saque realizado com sucesso! Seu saldo atual é: " + withdrawalAccountDTO.getBalance());
    }

    @PatchMapping("/{id}/deposito")
    @ApiOperation(value = "Efetua um depósito em uma conta")
    public ResponseEntity<Object> depositMoney(@RequestBody @Valid DepositDto depositDto, @PathVariable(value = "id") UUID id) {
        accountService.depositMoney(id, depositDto);
        return ResponseEntity.ok("Deposito realizado com sucesso! Seu saldo atual é: " + depositDto.getBalance());
    }

    @PatchMapping("/{id}/status")
    @ApiOperation(value = "Desativa uma conta alterando seu status para INATIVO")
    public ResponseEntity<Object> change(@RequestBody @Valid AccountStatus accountStatus, @PathVariable(value = "id") UUID id) {
        var account = accountService.changeStatusAccount(id, accountStatus);
        return ResponseEntity.ok(account);
    }

    @GetMapping
    @ApiOperation(value = "Exibe todas as contas")
    public ResponseEntity<Page<Account>> getAllAccounts(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Busca e exibe uma conta pelo id/chave primária")
    public ResponseEntity<Object> getOneAccount(@PathVariable(value = "id") UUID id) {
        Optional<Account> accountOptional = accountService.findById(id);
        if (!accountOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("conta não encontrada");
        }
        return ResponseEntity.status(HttpStatus.OK).body(accountOptional.get());
    }
}
