// Account Entity
package com.banking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    private int id;

    private String name;

    private double balance;

    public Account() {}
    public Account(int id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}

// AccountDao.java
package com.banking.dao;

import com.banking.entity.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Account getAccount(int id) {
        return entityManager.find(Account.class, id);
    }

    public void updateAccount(Account account) {
        entityManager.merge(account);
    }
}
// BankingService.java
package com.banking.service;

import com.banking.dao.AccountDao;
import com.banking.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankingService {

    @Autowired
    private AccountDao accountDao;

    @Transactional
    public void transferMoney(int fromId, int toId, double amount) {
        Account from = accountDao.getAccount(fromId);
        Account to = accountDao.getAccount(toId);

        if (from.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds.");
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        accountDao.updateAccount(from);
        accountDao.updateAccount(to);

        System.out.println("Transfer successful.");
    }
}
// AppConfig.java (Spring + Hibernate Config)
package com.banking.config;

import java.util.Properties;

import javax.sql.DataSource;

import com.banking.entity.Account;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.cj.jdbc.MysqlDataSource;

@Configuration
@ComponentScan("com.banking")
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL("jdbc:mysql://localhost:3306/your_db_name");
        ds.setUser("root");
        ds.setPassword("your_password");
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource());
        emf.setPackagesToScan("com.banking.entity");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);

        Properties props = new Properties();
        props.setProperty("hibernate.hbm2ddl.auto", "update");
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        props.setProperty("hibernate.show_sql", "true");

        emf.setJpaProperties(props);
        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory().getObject());
        return tm;
    }
}
//  MainApp.java
package com.banking;

import com.banking.config.AppConfig;
import com.banking.service.BankingService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        BankingService service = context.getBean(BankingService.class);

        try {
            service.transferMoney(1, 2, 1000.0);
        } catch (Exception e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        context.close();
    }
}
// SQL to Pre-Insert Accounts
INSERT INTO accounts (id, name, balance) VALUES (1, 'Alice', 5000.00);
INSERT INTO accounts (id, name, balance) VALUES (2, 'Bob', 3000.00);
