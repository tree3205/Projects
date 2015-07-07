mailBody(mail, empty, contents, unread, folderList, folderLinkList, folderName, attachList) ::= <<
    <nav class="navbar navbar-default navbar-fixed-top">
        <div class="container-fluid">
            <div class="navbar-header">
                <img class="welcome" src="/files/templates/welcome01.png">
            </div>
            <div id="navbar" class="navbar-collapse collapse">
                <ul class="nav navbar-nav navbar-right">
                    <li><a class="chgpwd" href="/changepwd">Change password</a></li>
                    <li><a class="signoff" href="/logout">Log out</a></li>
                </ul>
                <form class="navbar-form navbar-right" method="post" action="/search?page=1">
                    <input spellcheck="true" type="text" name="searchField" class="form-control" placeholder="Search...">
                </form>
            </div>
        </div>
    </nav>
    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-3 col-md-2 sidebar">
                <ul class="nav nav-sidebar">
                    <p class="compose">
                        <button type="button" class="composeButton btn btn-default" onClick="location.href='/sendMail';">COMPOSE MAIL<span class="glyphicon glyphicon-pencil"></span></button>
                    </p>
                    <p class="check">
                        <button type="button" class="btn btn-default" type="submit" onclick="location.href='/in?page=1';">CHECK MAIL<span class="glyphicon glyphicon-search"></button>
                    </p>
                </ul>
                <ul class="nav nav-sidebar">
                    <li><a href="/in?page=1">Inbox <span class="badge">$unread$</span></a></li>
                    <li><a href="/out?page=1">Outbox</a></li>
                    <li><a href="/trash?page=1">Trash</a></li>
                    <li><a href="/empty"><img src="/files/templates/trash.png" title="Empty Trash" width="18" height="18" /></a></li>
                </ul>
                <hr>
                <ul class="nav nav-sidebar">
                    <p class="add">
                        <div class="dropdown">
                            <button type="button" class="btn dropdown-toggle" id="myFolder" data-toggle="dropdown">Folders<span class="glyphicon glyphicon-chevron-down"></span>
                            </button>
                            <ul class="dropdown-menu pull-left" role="menu" aria-labelledby="myFolder">
                                $folderList: folder()$
                            </ul>
                        </div>
                    </p>
                    <p class="add">
                        <button type="button" class="btn btn-default" type="submit" onclick="location.href='/addMail';">Add Mail<span class="glyphicon glyphicon-plus-sign"></span></button>
                    </p>
                    <p class="rename">
                        <button type="button" class="btn btn-default" type="submit" onclick="location.href='/addFolder';">Add Folder<span class="glyphicon glyphicon-edit"></span></button>
                    </p>
                </ul>
            </div>
            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                <div class="MailDetail">
                    <div class="col-lg-9 col-lg-offset-2">
                        <div>
                            <h4 class="text-muted">From:   $mail.Sender$</h4>
                        </div>
                        <div>
                            <h4 class="text-muted">To:   $mail.Receiver$</h4>
                        </div>
                        <div>
                            <h4 class="text-muted">CC:   $mail.CC$</h4>
                        </div>
                        <div>
                            <h4 class="text-muted">Bcc:   $mail.Bcc$</h4>
                        </div>
                        <div>
                            <h4 class="text-muted">Subject:   $mail.Subject$</h4>
                        </div>
                        <div>
                            <h4 class="text-muted">Date:   $mail.Date$</h4>
                        </div>
                        <hr>
                        <div>
                            $contents:listItem()$
                            $attachList:listAttach()$
                        </div>
                        <div class="col-lg-2 col-lg-offset-0">
                            <button type="submit" class="reply btn btn-primary" onclick="location.href='/reply?id=$mail.MailID$';">Reply</button>
                        </div>
                        <div class="col-lg-2 col-lg-offset-1">
                            <button type="submit" class="reply btn btn-primary" onclick="location.href='/forward?id=$mail.MailID$';">Foward</button>
                        </div>
                        <div class="col-lg-2 col-lg-offset-3">
                            <button type="submit" class="reply btn btn-primary" onclick="location.href='$if(folderName)$/folder?type=$folderName$&page=1$else$/$mail.Type$?page=1$endif$';">Back</button>
                        </div>
                        <div class="col-lg-2 col-lg-offset-5">
                            <button type="submit" class="delete btn btn-primary" onclick="location.href='$if(!empty)$/deleteMail?id=$mail.MailID$$else$/clearMail?id=$mail.MailID$$endif$';">Delete</button>
                        </div>
                        <div class="col-lg-2 col-lg-offset-9 dropdown">
                            <button type="button" class="reply btn dropdown-toggle" id="saveTo" data-toggle="dropdown">Save To<span class="glyphicon glyphicon-chevron-down"></span>
                            </button>
                            <ul class="dropdown-menu pull-right" role="menu" aria-labelledby="saveTo">
                                $folderLinkList:saveToFolder()$
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Just to make our placeholder images work. Don't actually copy the next line! -->
    <script src="/files/templates/home_files/holder.js"></script>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="/files/templates/home_files/ie10-viewport-bug-workaround.js"></script>
>>