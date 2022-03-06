# Loan Service

Build a service for simulating loan processing that supports the following operations through some network based interface (e.g. rest or graphQL):

Initiate loan: with arguments for initial amount, annual interest rate, and start date.
Add payment: with arguments for amount and date.
Get balance: takes a date as an argument and returns the total balance as of that date.
It should be possible to add payments in any order and request the balance of any date. The balance returned should be based on all payments and the interest added up to the requested date. The interest should not be compounding, i.e. the interest calculation should be based on the principal balance and exclude already added interest balance. The interest added for a day is defined as: annual interest rate / 100 / 365 * principal balance.

It is sufficient to keep the state in-memory (no DB required) and the service does not need to handle multiple loans, i.e. the state can be cleared when a new loan is initiated.

You are free to make your own decisions on unspecified details. Send in your implementation and a brief description of the main decisions/assumptions made and how to run your application.


### Decisions/Assumptions
* Loan Term is 12 months
* Payments has been done for monthly basis
* Interest Rate : Reducing Interest Rate
* Balance Rate : Reducing Balance Method
* principleBalance : Is the amount still owed on the original amount financed without any interest or finance charges that are due.
* interest paid for each month = loan_balance x interest% / 12
* Interest added for a day =  annual interest rate / 100 / 365 * principal balance
* Remaining Balance = Interest added for a day * remaining dates for loan
* BigDecimal has been used monetary calculation because its giving best support for specifying and preserving what can be highly variable precision.
* Simple H2 DataBase was introduce to keep the data in the database.
H2 DB is written to a file as all data will be there for each application startup. 
H2 DB : jdbc:h2:./data/db

## Installation

### Build the Project

```bash
mvn clean install
```
### Run the Application

You can run the project using command line like below and also can checkout to any IDE and run the application.

```bash
mvn spring-boot:run
```

## Create Loan
#### Sample Payload
```
curl --location --request POST 'http://localhost:8080/loan' \
--header 'Content-Type: application/json' \
--data-raw '{
   "initialAmount":10000,
   "interestRate": 10,
   "startDate":"2021-10-01"    
}'
```
#### Sample Output
```
{
    "loanId": 9
}
```

## Make Payment
#### Sample Payload
```
curl --location --request POST 'http://localhost:8080/loan/pay' \
--header 'Content-Type: application/json' \
--data-raw '{
   "payment":8333.33,
   "loanId":9,
   "paymentDate":"2021-12-10"    
}'
```
#### Sample Output
```
{
    "loanId": 9,
    "paymentId": 14
}
```

## Get Balance for the Date
#### Sample Payload
id = loan id
date = requested the balance for the date
```
curl --location --request GET 'http://localhost:8080/loan/?id=9&date=2022-05-04'
```
#### Sample Output
```
{
    "loanInitialAmount": 100000.00,
    "totalBalanceToDate": 80489.19,
    "interestRate": 10.0,
    "startDate": "2021-09-30T18:30:00.000+00:00"
}
```

