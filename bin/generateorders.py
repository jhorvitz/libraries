#!/usr/bin/python
import sys
import random
import math
import sqlite3
conn = sqlite3.connect("orderhistory.db")
c = conn.cursor()
itemsFile = sys.argv[1]
numCustomers = int(sys.argv[2])
numOrders = int(sys.argv[3])
minOrderNo = 10 ** int(math.log10(numOrders))
minCustID = 10 ** int(math.log10(numCustomers))
out = open("orderhistory.sql", "w")
c.execute('''CREATE TABLE items
            (itemid INTEGER PRIMARY KEY, itemname TEXT, itemprice REAL)''')
c.execute('''CREATE TABLE orders
            (orderid INTEGER PRIMARY KEY, date TEXT, customerid INTEGER, total REAL)''')
c.execute('''CREATE TABLE orderitems
            (orderid INTEGER, itemid INTEGER, quantity INTEGER, FOREIGN KEY(orderid) REFERENCES orders(orderid), FOREIGN KEY(itemid) REFERENCES items(itemid))''')
items = []
class Item:
    def __init__(self, line):
        fields = line.rstrip().split(",")
        self.id = fields[0]
        self.name = fields[1]
        self.price = float(fields[2])
with open(itemsFile, "r") as reader:
    next(reader)
    for line in reader:
        item = Item(line)
        items.append(item)
        c.execute("INSERT INTO items VALUES ('" + item.id + "','" + item.name + "','" + str(item.price) + "')")
def toPaddedStr(num):
    if num < 10:
        return "0" + str(num)
    else:
        return str(num)
with conn:
    for i in range(numOrders):
        orderNo = str(minOrderNo + i)
        cust = random.randint(0, numCustomers - 1) + minCustID
        month = random.randint(1, 12)
        monthstr = toPaddedStr(month)
        day = random.randint(1, 28)
        daystr = toPaddedStr(day)
        year = random.randint(2000, 2014)
        n = int(len(items) - math.log(random.randint(1, int(1.5 ** len(items))), 1.5)) + 1
        date = str(year) + "-" + monthstr + "-" + daystr
        hour = random.randint(0, 23)
        hourstr = toPaddedStr(hour)
        min = random.randint(0, 59)
        minstr = toPaddedStr(min)
        sec = random.randint(0, 59)
        secstr = toPaddedStr(sec)
        time = hourstr + ":" + minstr + ":" + secstr
        total = 0
        itemsSeen = []
        for j in range(n):
            itemNo = random.randint(0, len(items) - 1)
            if itemNo in itemsSeen:
                continue
            itemsSeen.append(itemNo)
            quantity = int(12 - math.log(random.randint(1, 4 ** 12), 4)) + 1
            total += items[itemNo].price * quantity
            c.execute("INSERT INTO orderitems VALUES ('" + orderNo + "','" + items[itemNo].id + "','" + str(quantity) + "')")
        c.execute("INSERT INTO orders VALUES ('" + orderNo + "','" + date + " " + time + "','" + str(cust) + "','" + str(total) + "')")
conn.close()
print "Database successfully outputted to orderhistory.db"
