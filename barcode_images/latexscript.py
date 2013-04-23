#!/usr/bin/python
import dircache
#specify the source path of the images
__file__="C:\Users\RSJ\Documents\GitHub\corobot-extras\\barcode_images"
list = dircache.listdir(__file__)
i = 0
check = len(list[0])
temp = []
count = len(list)
f1=open("print.tex", "w+")
suffix = ".png";

print >> f1 , "\\documentclass[letterpaper]{article} \n \\usepackage[pdftex]{graphicx} \n \\pagenumbering{gobble} \n \\begin{document} "


while count != 0:
      if(list[i].endswith(suffix)):
        pre="\\topskip0pt \n \\vspace*{\\fill} \n \\begingroup \n \\centerline{\\includegraphics[scale=1,width=5in,height=5in]{";
        middle=list[i];
        post= "}} \n \\endgroup \n \\vspace*{\\fill} \n \\pagebreak \n"
        final=''.join([pre, middle, post])
        print >> f1, final ,
      i=i+1
      count=count-1

print >> f1, "\\end{document}"

f1.close()
