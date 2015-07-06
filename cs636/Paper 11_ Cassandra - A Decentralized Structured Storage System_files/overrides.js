/* define your menu here

Some design thoughts:
		* More than about a dozen items is too tall for older screens
		* More than one column is often too wide for older screens
		* Subtitles are nice... but hard to read and make entries taller
*/
var colorStripe = ''; //'background: #fffffe; border-bottom: #9FA7AF solid 1px; border-right: #9FA7AF solid 1px; border-top: #ffffff solid 1px; border-left: #ffffff solid 1px;';
var resources = {
	title: 'USF Resources', // required
	// url: optional,
	// target: optional,
	// an array of columns to display (don't go overboard here!)
	columns: [
		{
			// columns can be divided into titled sections
			sections: [
				
				
				{
					title: '',
					// style: colorStripe,
					items: [
						{
							title: 'Library',
							url: 'http://www.usfca.edu/library/',
							subtitle: 'Online Resources, References',
							target: '_blank'
						},
						{
							title: 'Help with Canvas',
							url: 'http://www.usfca.edu/its/learning/canvas/home/',
							subtitle: 'Need help?',
							target: '_blank'
						},
						{
							title: 'Teaching Effectiveness Survey',
							url: 'https://usfca.bluera.com/usfca',
							target: '_blank'
						}
					]
				}
			
			]
		}
	]
};






//parse the array/object structure above into the HTML that represents a dropdown menu and add it to the right of the existing menubar
function usf_appendMenu(m) {
	var i, j, k;

	var navigationMenu = document.getElementById("menu");
	var menu = document.createElement('li');
	menu.setAttribute('class', 'menu-item');
	var html = '<a class="menu-item-title"' + (m.url !== undefined ? ' href="' + m.url + '"' : '') + (m.target !== undefined ? ' target="' + m.target + '"' : '') + '>' + m.title + '<span class="menu-item-title-icon"/> <i class="icon-mini-arrow-down"/></a>';

	html += '<div class="menu-item-drop"><table cellspacing="0"><tr>';

	for(i = 0; i < m.columns.length; i++) {
		
		{
			html += '<td class="menu-item-drop-column"' + (m.columns[i].style !== undefined ? ' style="' + m.columns[i].style + '"': '') + '>';
			for (j = 0; j < m.columns[i].sections.length; j++) {
				
					{
					html += (m.columns[i].sections[j].title !== undefined ? '<span class="menu-item-heading"' + (m.columns[i].sections[j].style !== undefined ? ' style="' + m.columns[i].sections[j].style + '"' : '') + '>' + m.columns[i].sections[j].title + '</span>' : '');
					html += '<ul class="menu-item-drop-column-list"' + (m.columns[i].sections[j].style !== undefined ? ' style="' + m.columns[i].sections[j].style + '"' : '') + '>';

					for (k = 0; k < m.columns[i].sections[j].items.length; k++) {
						
						{
							html += '<li' + (m.columns[i].sections[j].items[k].style !== undefined ? ' style="' + m.columns[i].sections[j].items[k].style + '"' : '') + '><a' + (m.columns[i].sections[j].items[k].target !== undefined ? ' target="' + m.columns[i].sections[j].items[k].target + '"' : '') + (m.columns[i].sections[j].items[k].url !== undefined ? ' href="' + m.columns[i].sections[j].items[k].url + '"' : '') + '><span class="name ellipsis">' + m.columns[i].sections[j].items[k].title + '</span>' + (m.columns[i].sections[j].items[k].subtitle !== undefined ? '<span class="subtitle">' + m.columns[i].sections[j].items[k].subtitle + '</span>' : '') + '</a></li>';
						}
					}
					html += '</ul>';
				}
			}
			html += '</td>';
		}
	}
	html += '</tr></table></div>';
	menu.innerHTML = html;
	navigationMenu.appendChild(menu);
}

function usf_courseEvalMenu() {
	userID = ENV.current_user_id;
	$('#menu_enrollments .menu-item-drop-column-list').append('<li id="faccourseeval_menu_item" class="customListItem"><a href="/users/' + userID + '/external_tools/' + '108864' + '"><span class="name ellipsis" title="Teaching Effectiveness Survey Reports">Teaching Effectiveness Survey Reports</span></a></li>');
	$('#menu_enrollments .menu-item-drop-column-list').append('<li id="stucourseeval_menu_item" class="customListItem"><a href="/users/' + userID + '/external_tools/' + '108863' + '"><span class="name ellipsis" title="Teaching Effectiveness Survey">Teaching Effectiveness Survey</span></a></li>');

}

function usf_resourcesMenu() {

		usf_appendMenu(resources);
		usf_courseEvalMenu();

}


onPage(/\/courses\/\d+\/settings/, function() {
  // do something
	
	
});



hasAnyRole('admin', function(hasRole) {
  if (hasRole) {
    // do something
  } else {

    // do something else
    // a[href=#create_ticket]
onElementRendered('a[href=#create_ticket]', function(el) {
  // do something with el (a jquery element collection)
  el.parent().remove();
});

  }
});
  

 
function onPage(regex, fn) {
  if (location.pathname.match(regex)) fn();
}
 
function hasAnyRole(/*roles, cb*/) {
  var roles = [].slice.call(arguments, 0);
  var cb = roles.pop();
  for (var i = 0; i < arguments.length; i++) {
    if (ENV.current_user_roles.indexOf(arguments[i]) !== -1) {
      return cb(true);
    }
  }
  return cb(false);
}
 
function isUser(id, cb) {
  cb(ENV.current_user_id == id);
}
 
function onElementRendered(selector, cb, _attempts) {
  var el = $(selector);
  _attempts = ++_attempts || 1;
  if (el.length) return cb(el);
  if (_attempts == 60) return;
  setTimeout(function() {
    onElementRendered(selector, cb, _attempts);
  }, 250);
}

$(document).ready(function(){
  // Hide the link by default
  $('a.delete_course_link').hide();
  $('a.reset_course_content_button').hide();
 //Hide Blackboard Archive link by default
  $(".context_external_tool_92759").hide();
//Hide user setting links to course evaluations
  $('a.context_external_tool_108864').hide();
  $('a.context_external_tool_108863').hide();

 
 usf_resourcesMenu(); 
  $('#faccourseeval_menu_item').hide();
  $('#stucourseeval_menu_item').hide();
  
  for(x=0;x<ENV.current_user_roles.length;x++){
    // Show the delete link if the user is found to be an admin somewhere in Canvas, which
    // is the only way they would have the role of 'admin' in this javascript object. One
    // caveat, the user could theoretically be a teacher in the current course but because
    // the user is an admin somewhere else they will have this role listed in the array.
    if(ENV.current_user_roles[x]=='admin'){
      $('a.delete_course_link').show();
      $('a.reset_course_content_button').show();
    }
    else if(ENV.current_user_roles[x]=='teacher'){
      //show Blackboard Archive link if teacher role
      $(".context_external_tool_92759").show();
      $('#faccourseeval_menu_item').show();
    }
    else if(ENV.current_user_roles[x]=='student'){
      $('#stucourseeval_menu_item').show();
    }
  }  
}); 








