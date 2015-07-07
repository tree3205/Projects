homeBody(mails, inbox, outbox, trash, unread, page, currentPage, sortBy, order,folderList, folderName, ifSort) ::= <<
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
                    <li><a href="/in?page=1">$if(inbox)$<Strong>$endif$Inbox$if(inbox)$</Strong>$endif$ <span class="badge">$unread$</span></a></li>
                    <li><a href="/out?page=1">$if(outbox)$<Strong>$endif$Outbox$if(outbox)$</Strong>$endif$</a></li>
                    <li><a href="/trash?page=1">$if(trash)$<Strong>$endif$Trash$if(trash)$</Strong>$endif$</a></li>
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
                <div class="table-responsive">
                    <!-- Table has 15 rows -->
                    <table class="table table-striped">
                        $if(folderName)$<caption><h3 class="text-center"><strong>$folderName$</strong></h3></caption>$endif$
                        <thead>
                            <tr>
                                <p>
                                    <th>
                                        $if(ifSort)$
                                        <div class="dropdown">
                                            <button type="button" class="btn-custom dropdown-toggle btn-xs" id="dropdownMenu1" data-toggle="dropdown">
                                                <label>
                                                    <p>Sort by</p>
                                                </label>
                                                <span class="caret"></span>
                                            </button>
                                            <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
                                                <li role="presentation">
                                                    <a role="menuitem" tabindex="-1" href="/$page.type$sort=sender&order=ASC&page=$currentPage$">Sender<Span class="glyphicon glyphicon-arrow-up"></span></a>
                                                </li>
                                                <li role="presentation">
                                                    <a role="menuitem" tabindex="-1" href="/$page.type$sort=sender&order=Desc&page=$currentPage$">Sender<Span class="glyphicon glyphicon-arrow-down"></span></a>
                                                </li>
                                                <li role="presentation">
                                                    <a role="menuitem" tabindex="-1" href="/$page.type$sort=subject&order=ASC&page=$currentPage$">Subject<Span class="glyphicon glyphicon-arrow-up"></a>
                                                </li>
                                                <li role="presentation">
                                                    <a role="menuitem" tabindex="-1" href="/$page.type$sort=subject&order=Desc&page=$currentPage$">Subject<Span class="glyphicon glyphicon-arrow-down"></a>
                                                </li>
                                                <li role="presentation">
                                                    <a role="menuitem" tabindex="-1" href="/$page.type$sort=emailDate&order=ASC&page=$currentPage$">Date<Span class="glyphicon glyphicon-arrow-up"></a>
                                                </li>
                                                <li role="presentation">
                                                    <a role="menuitem" tabindex="-1" href="/$page.type$sort=emailDate&order=Desc&page=$currentPage$">Date<Span class="glyphicon glyphicon-arrow-down"></a>
                                                </li>
                                            </ul>
                                        </div>
                                        $endif$
                                    </th>
                                </p>
                            </tr>
                        </thead>
                        <tbody>
                            $mails:row()$
                        </tbody>
                    </table>
                </div>
                $if(page.pageExist)$
                <div class="page">
                    <ul class="pagination pagination">
                        $if(page.previouspre)$<li><a class="pageNo" href="/$page.type$$if(sortBy)$sort=$sortBy$&$endif$$if(order)$order=$order$&$endif$page=$page.PreviousPreviousPageNo$">&laquo;</a></li>$endif$
                        $if(page.previous)$<li><a class="pageNo" href="/$page.type$$if(sortBy)$sort=$sortBy$&$endif$$if(order)$order=$order$&$endif$page=$page.PreviousPageNo$">$page.PreviousPageNo$</a></li>$endif$
                        <li><a class="pageNo" href="/$page.type$$if(sortBy)$sort=$sortBy$&$endif$$if(order)$order=$order$&$endif$page=$page.pageNo$">$page.pageNo$</a></li>
                        $if(page.next)$<li><a class="pageNo" href="/$page.type$$if(sortBy)$sort=$sortBy$&$endif$$if(order)$order=$order$&$endif$page=$page.NextPageNo$">$page.NextPageNo$</a></li>$endif$
                        $if(page.nextnext)$<li><a class="pageNo" href="/$page.type$$if(sortBy)$sort=$sortBy$&$endif$$if(order)$order=$order$&$endif$page=$page.NextNextPageNo$">&raquo;</a></li>$endif$
                    </ul>
                </div>
                $endif$
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