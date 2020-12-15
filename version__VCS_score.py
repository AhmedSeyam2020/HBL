import pickle
import json
import numpy as np
from sklearn import preprocessing
import glob
from datasets import DATASET
import os
import math as m


with open(DATASET.root / 'preprocessed_src.pickle', 'rb') as file:
        src_files = pickle.load(file)
with open(DATASET.root / 'preprocessed_reports.pickle', 'rb') as file:
        bug_reports = pickle.load(file)
scores = []

filenames = glob.glob('./zxing/*.txt')
#print(filenames)
  
for f in filenames:
     matched_cc = []
     outfile = open(f,'r')
     lines=outfile.readlines()
     
     for src in src_files.values():
        src_score=0  
        for line in lines:
            data=line.split(".")
            if(data[2]!='None'):
             cc=int(data[2])
             diff=int(data[3])
            if src.file_name['stemmed']:
             if (src.exact_file_name==data[0]):
                if(cc>45):
                 src_score +=1 / (1+m.exp(12*(1-(90-diff)/90)))
        matched_cc.append(src_score)   
             
             
     min_max_scaler = preprocessing.MinMaxScaler()
        
     cc_count = np.array([float(count)
          for count in matched_cc]).reshape(-1, 1)
     normalized_count = np.concatenate(
         min_max_scaler.fit_transform(cc_count)
        )
        
     scores.append(normalized_count.tolist())   
    
with open(DATASET.root / 'Version_cc.json', 'w') as file:
     json.dump(scores, file)      
   
   
    
  