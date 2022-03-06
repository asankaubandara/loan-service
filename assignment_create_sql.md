# Assignment Creating SQL Queries

Given the database tables:

Persons: id, name
Incomes: id, person_id, date, amount
Expenses: id, person_id, date, amount
Write SQL queries that answers the questions below. Send in the queries in your response.


## a. Which person has the greatest total expense amount?
#### Query
```
select 
  p.name, 
  totalsum 
from 
  (
    select 
      expenses.person_id as person, 
      sum(expenses.amount) as totalsum 
    from 
      expenses 
    group by 
      expenses.person_id
  ) expense 
  inner join persons p on expense.person = p.id 
order by 
  totalsum desc 
limit 
  1;
```
#### Output
```
     Name       |     totalsum
Benjamin Davies |	1069824.17
```

## b. Which person has the greatest total end balance considering all incomes and expenses?
#### Query
```
select 
  persons.name, 
  (
    tot_income_tab.total_income - tot_expense_tab.total_expense
  ) as end_balance 
from 
  persons 
  inner join (
    select 
      expenses.person_id as person, 
      sum(expenses.amount) as total_expense 
    from 
      expenses 
    group by 
      expenses.person_id
  ) tot_expense_tab on persons.id = tot_expense_tab.person 
  inner join (
    select 
      incomes.person_id as person, 
      sum(incomes.amount) total_income 
    from 
      incomes 
    group by 
      incomes.person_id
  ) tot_income_tab on persons.id = tot_income_tab.person 
order by 
  end_balance desc 
limit 
  1;
```
#### Output
```
     Name        |     end_balance
Cameron Anderson |	248291.25
```

## List the name, date and balance of the three persons with the highest peak balances, i.e. the day when all incomes and expenses up to that day gives the highest balance.
#### Query

```
select 
  transaction_by_day_person.transaction_date, 
  transction_user.name, 
  transaction_by_day_person.end_balance 
from 
  persons transction_user 
  inner join (
    select 
      total_income_by_person_date.date as transaction_date, 
      total_income_by_person_date.person as transactio_person, 
      total_income_by_person_date.person_income_for_date - total_expense_by_person_date.person_expense_for_date as end_balance 
    from 
      (
        select 
          transaction_date_tab.date as date, 
          incomes.person_id as person, 
          sum(incomes.amount) person_income_for_date 
        from 
          incomes 
          inner join (
            select 
              distinct(date) 
            from 
              incomes 
            UNION 
            select 
              distinct (date) 
            from 
              expenses 
            order by 
              date
          ) transaction_date_tab on incomes.date = transaction_date_tab.date 
        group by 
          person, 
          transaction_date_tab.date
      ) total_income_by_person_date 
      inner join (
        select 
          transaction_date_tab.date as date, 
          expenses.person_id as person, 
          sum(expenses.amount) as person_expense_for_date 
        from 
          expenses 
          inner join (
            select 
              distinct(date) 
            from 
              incomes 
            UNION 
            select 
              distinct (date) 
            from 
              expenses 
            order by 
              date
          ) transaction_date_tab on expenses.date = transaction_date_tab.date 
        group by 
          person, 
          transaction_date_tab.date
      ) total_expense_by_person_date on (
        total_income_by_person_date.date = total_expense_by_person_date.date 
        and total_income_by_person_date.person = total_expense_by_person_date.person
      )
  ) transaction_by_day_person on transction_user.id = transaction_by_day_person.transactio_person 
order by 
  transaction_by_day_person.end_balance desc 
limit 
  3
```
#### Output
```
transaction_date |   name      |  end_balance 
2019-10-14	     | Jack Slater |  7352.72
2019-01-08	     | Dan Watson  |  6361.98
2019-08-23	     | Donna Lyman |  6132.89
```

