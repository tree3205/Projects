addFolderBody() ::= <<
    <div class="container">
        <div class="row">
            <div class="col-lg-8 col-lg-offset-2">
                <form id="addMailForm" method="post" class="form-horizontal" action="/addFolderSubmit">
                    <div class="page-header">
                        <h1 class="text-primary">Please add a new folder</h1>
                    </div>
                    <div class="form-group">
                        <label class="col-lg-3 control-label">Folder name</label>
                        <div class="col-lg-5">
                            <input spellcheck="true" type="text" class="form-control" name="folderName"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-lg-9 col-lg-offset-3">
                            <button type="submit" class="btn btn-primary">Save Folder</button>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-lg-9 col-lg-offset-3">
                            <button class="send btn btn-primary" type="button" onclick="location.href='/in?page=1';">Back</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
>>