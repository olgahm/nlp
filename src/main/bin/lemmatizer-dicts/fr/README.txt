 
DICTIONARIES FOR LEMMATIZATION
--------------------------------
This dictionary is based on Lefff (Lexique des Formes Fléchies du Français) a dictionary distributed
under the LGPL-LR (Lesser General Public License For Linguistic Resources) license. Please do
be aware of this license when you use the French pos tagger and lemmatizer (see COPYING file distributed
in this directory).

Also, if you use the French lemmatizer cite this paper:

Référence principale: Sagot (2010). The Lefff, a freely available and large-coverage morphological and syntactic lexicon for French. In Proceedings of the 7th international conference on Language Resources and Evaluation (LREC 2010), Istanbul, Turkey

The format of the dictionaries consist of "word\tab\lemma\tabpos" and it can be 
used to perform dictionary-based lemmatization. They are distributed as 
spanish.dict, english.dict, etc., in a finite state automata format created 
using Morfologik (https://github.com/morfologik/). These dictionaries need 
the corresponding $lang.info files to work. 

Note that plain text dictionaries can also be used in their current form
via the API (check SimpleLemmatizer class). 

Contact details: 
----------------
Rodrigo Agerri <rodrigo.agerri@ehu.eus>
IXA NLP Group
University of the Basque Country (UPV/EHU)
