cs601-webmail

Name: yxu66


Delivery:
1. Finish all the basic requirements:
	a. User registration.
	b. User log in / out.
	c. Log each page request and also create log files.
	d. Pull email from POP servers via my own POP protocol.
	e. Mail pulled from POP is stored in the database.
	f. Inbox to display incoming mail.
	g. View mail message page.
	h. Check mail button.
	i. Compose message page.
	j. Send / reply to email.
	k. Forward email.
	l. Delete mail in view mail page and can empty the entire 	 trash by clicking "empty trash button".
	m. Support HTTPS connections to my website.
	n. An indication for read / unread mail.
	o. pagination
	p. Sent mail goes to inbox page.
	q. Change password
	r. Users can direct all the mail whether in inbox, outbox or trash to specific folder. User can also add new folder.
	s. Sort mail by sender, subject and date ascending or descending.
2. Feature: 
	a. spell check
	b. Use of Lucene to search
	c. Attachments. 

==============================================================

Data Base Structure:

1. Users table to store the registered user information.
2. EmailAccount table to store every email account, smtp, pop server and ports provided by users.
3. Mails table to store all the mails including sender, receiver, cc, bcc, subject, content, received date, uidl, status(read/unread), folderID.
4. Attachments table to save which the attachments belongs to and saved file name and file path.
5. Folders table to save different folder created by users.

==============================================================

Project structure:

There are three servers:
1. Webmail server
The webmail server start the whole web mail service, when it start, the lucene service will also start, it will read from Mails table in data base to create the search content for later use.
2. SMTP server: use smtp.gmail.com to send mail and it implement sending to cc, bcc as well as attachments function.
3. POP server: pull all the mails from all email accounts under the user account. Before pulling emails, it will contact with Mails table in data base to check the uidl. Only those mails whose uidl haven't been stored in data base will need to pull from pop server. After pulling mails, return a list of mails so that can save to data base later.

There are three objects in projects:
1. User
2. Email
3. Attachment
these objects will in the response of data transmission from data base to Model verse verso.

Model:
Having UserModel, EmailModel, FolderModel and corresponding manager to communicate with data base to store data in it or retrieve data from it. DBManager just respond to data base connection.

Other Supplement like Pagination and SearchService to provide the function of pagination and lucene search.

Different pages in charge of different requests from browser and after communicate with data base and then do according response. Especially, when each time into inbox page, it will pull from pop server so that the mail will received in time.







