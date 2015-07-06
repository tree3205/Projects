homeBody() ::= <<
    <nav class="navbar navbar-default navbar-fixed-top">
        <div class="container-fluid">
            <div class="navbar-header">
                <img class="welcome" src="/files/templates/welcome01.png">
            </div>
            <div id="navbar" class="navbar-collapse collapse">
                <ul class="nav navbar-nav navbar-right">
                    <!-- need to change to other links -->
                    <li><a class="chgpwd" href="/files/templates/home_files/home.html">Change password</a></li>
                    <li><a class="signoff" href="/logout">Log out</a></li>
                </ul>
                <form class="navbar-form navbar-right">
                    <input type="text" class="form-control" placeholder="Search...">
                </form>
            </div>
        </div>
    </nav>
    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-3 col-md-2 sidebar">
                <ul class="nav nav-sidebar">
                    <p class="compose">
                        <button type="button" class="composeButton btn btn-default" onClick="window.open('/sendMail', '_blank', 'top=100, left=350, width=800, height=600');">COMPOSE MAIL<span class="glyphicon glyphicon-pencil"></span></button>
                    </p>
                    <p class="check">
                        <button type="button" class="btn btn-default" type="submit">CHECK MAIL<span class="glyphicon glyphicon-search"></button>
                    </p>
                </ul>
                <ul class="nav nav-sidebar">
                    <!-- Should have a variable of num of message -->
                    <!-- And check where the place is, if in inbox, make it Strong -->
                    <li><a href="/files/templates/home_files/home.html"><Strong>Inbox </Strong><span class="badge">50</span></a></li>
                    <li><a href="/files/templates/home_files/home.html">Outbox</a></li>
                    <li><a href="/files/templates/home_files/home.html">Drafts</a></li>
                    <li><a href="/files/templates/home_files/home.html">Trash</a></li>
                </ul>
                <hr>
                <ul class="nav nav-sidebar">
                    <li><a href="">Contacts</a></li>
                    <li><a href="">My Folder</a></li>
                    <p class="add">
                        <button type="button" class="btn btn-default" type="submit">Add<span class="glyphicon glyphicon-plus-sign"></span></button>
                    </p>
                    <p class="rename">
                        <button type="button" class="btn btn-default" type="submit">Rename<span class="glyphicon glyphicon-edit"></span></button>
                    </p>
                </ul>
            </div>
            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                <div class="table-responsive">
                    <!-- Table has 15 rows -->
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <p>
                                    <th>
                                        <div class="dropdown">
                                            <button type="button" class="btn-custom dropdown-toggle btn-xs" id="dropdownMenu1" data-toggle="dropdown">
                                                <label>
                                                    <input type="checkbox" value="">
                                                </label>
                                                <span class="caret"></span>
                                            </button>
                                            <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
                                                <li role="presentation">
                                                    <a role="menuitem" tabindex="-1" href="#">All</a>
                                                </li>
                                                <li role="presentation">
                                                    <a role="menuitem" tabindex="-1" href="#">None</a>
                                                </li>
                                                <li role="presentation">
                                                    <a role="menuitem" tabindex="-1" href="#">Read</a>
                                                </li>
                                                <li role="presentation">
                                                    <a role="menuitem" tabindex="-1" href="#">Unread</a>
                                                </li>
                                                <li role="presentation">
                                                    <a role="menuitem" tabindex="-1" href="#">Starred</a>
                                                </li>
                                                <li role="presentation">
                                                    <a role="menuitem" tabindex="-1" href="#">Unstarred</a>
                                                </li>
                                            </ul>
                                        </div>
                                    </th>
                                </p>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>Lorem</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>sit</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>amet</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>elit</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>Integer</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>Praesent</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>libero</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>ante</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>dapibus</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>nisi</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>Nulla</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>at</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>nibh</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>Duis</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>sagittis</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>mauris</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>Fusce</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>sed</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>augue</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>Mauris</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>massa</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>arcu</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>eget</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>aptent</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>taciti</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>litora</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>torquent</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>nostra</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>
                                        <input type="checkbox" value="">
                                    </label>
                                </td>
                                <td>per</td>
                                <td colspan="3">likjlafnkndkdfijgula</td>
                                <td>Curabitur</td>
                            </tr>
                        </tbody>
                    </table>
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

    <svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200" preserveAspectRatio="none" style="visibility: hidden; position: absolute; top: -100%; left: -100%;">
        <defs></defs>
        <text x="0" y="10" style="font-weight:bold;font-size:10pt;font-family:Arial, Helvetica, Open Sans, sans-serif;dominant-baseline:middle">200x200</text>
    </svg>
>>