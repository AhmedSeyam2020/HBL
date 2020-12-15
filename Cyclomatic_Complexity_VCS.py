"""
from pydriller import RepositoryMining
import datetime
import pickle
import json
import numpy as np
from sklearn import preprocessing
import dateutil.relativedelta
import glob, os


from datasets import DATASET


with open(DATASET.root / 'preprocessed_reports.pickle', 'rb') as file:
        bug_reports = pickle.load(file)
        
        for report in bug_reports.values():    
            d1=str(report.opendate)
            f = open(report.id+".txt", "w")
            
            dt1 = datetime.datetime.strptime(d1, "%Y-%m-%d %H:%M:%S")
            dt2 = dt1 - dateutil.relativedelta.relativedelta(months=3)
            #print(dt1)
        
            for commit in RepositoryMining('repo/eclipse.platform.swt', since=dt2, to=dt1 ).traverse_commits():
               comm_date=commit.committer_date.replace(tzinfo=None)
               diff=dt1-comm_date
               #print(diff.days)
               for m in commit.modifications:
                   file_java=str(m.filename)
                   
                 
                   if ".java" in file_java :
                      f.write(m.filename+"."+str(m.complexity)+"."+str(diff.days)+'\n')
                  
                     
    """
                      