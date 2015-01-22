# -*- coding: utf-8 -*-
import xlrd
import datetime
import csv
from os import sys

def cell_value(cell, datemode):
    if cell.ctype == xlrd.XL_CELL_DATE:
        if cell.value != 0.0:
            t = xlrd.xldate_as_tuple(cell.value, datemode)
            if t[0] > 0:
                val = datetime.datetime(*t)
            else:
                val = datetime.time(*t[3:])
        else:
            val = EPOCH
    else:
        val = cell.value
        if isinstance(val, float):
            if int(val) == val:
                # int masquerading as a float?
                val = int(val)
    return val

#cell_value(a_cell, workbook.datemode)


def csv_from_excel(excel_file):
    workbook = xlrd.open_workbook(excel_file)
    all_worksheets = workbook.sheet_names()
    for worksheet_name in all_worksheets:
        worksheet = workbook.sheet_by_name(worksheet_name)
        your_csv_file = open(''.join([worksheet_name,'.csv']), 'wb')
        wr = csv.writer(your_csv_file, quoting=csv.QUOTE_ALL)

        for rownum in xrange(worksheet.nrows):
            row = []
            for colnum in xrange(worksheet.ncols):
                row.append(worksheet.cell(rownum, colnum))
            s2 = [unicode(cell_value(cell, workbook.datemode)).encode("utf-8") for cell in row]
     
            wr.writerow(s2)
        your_csv_file.close()


if __name__ == "__main__":
    csv_from_excel(sys.argv[1])
