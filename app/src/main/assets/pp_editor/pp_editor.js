window.Initial_Primary_X = 0;
window.Initial_Primary_Y = 0;
window.Initial_Width = 0;
window.Initial_Height = 0;

function Set_Profile_Pic(Path, Width, Height)
{
    var profile_pic_container = document.getElementById('profile_pic_container');
    var profile_pic = document.getElementById('profile_pic');

    profile_pic.style.backgroundImage = "url(" + Path + ")";
    profile_pic.style.width  = Width + "px";
    profile_pic.style.height = Height + "px";

    profile_pic_container.style.width  = document.body.clientWidth + "px";
    profile_pic_container.style.height = document.body.clientWidth + "px";
}

function Set_Initial_Primary_XY()
{
    var profile_pic = document.getElementById('profile_pic');
    var offsets = profile_pic.getBoundingClientRect();

    window.Initial_Primary_X = offsets.left;
    window.Initial_Primary_Y = offsets.top;

    window.Initial_Width = parseInt(profile_pic.style.width.replace("px", ""));
    window.Initial_Height = parseInt(profile_pic.style.height.replace("px", ""));
}

function Set_Initial_Dimensions()
{
    var profile_pic = document.getElementById('profile_pic');

    window.Initial_Width = parseInt(profile_pic.style.width.replace("px", ""));
    window.Initial_Height = parseInt(profile_pic.style.height.replace("px", ""));
}

function Action_Move(Diff_X, Diff_Y)
{
    var left = Initial_Primary_X - Diff_X;
    var top  = Initial_Primary_Y - Diff_Y;
    var container_dim = parseInt(document.getElementById('profile_pic_container').style.width.replace("px", ""));

    if(left > 0) left = 0;
    if(top  > 0) top = 0;

    if((left + Initial_Width) < container_dim) left = container_dim - Initial_Width;
    if((top + Initial_Height) < container_dim) top  = container_dim - Initial_Height;

    profile_pic.style.left = left + 'px';
    profile_pic.style.top  = top  + 'px';
}

function Action_Scale(Scale)
{
    var profile_pic = document.getElementById('profile_pic');
    var container_dim = parseInt(document.getElementById('profile_pic_container').style.width.replace("px", ""));

    var width  = Math.round(Initial_Width * Scale);
    var height = Math.round(Initial_Height * Scale);

    var left = Initial_Primary_X + Math.round((Initial_Width - width) / 2);
    var top  = Initial_Primary_Y + Math.round((Initial_Height - height) / 2);

    if(width >= container_dim && height >= container_dim)
    {
        if(left > 0) left = 0;
        if(top  > 0) top = 0;

        if((left + width) < container_dim) left = container_dim - width;
        if((top + height) < container_dim) top  = container_dim - height;

        profile_pic.style.width  = width  + "px";
        profile_pic.style.height = height + "px";

        profile_pic.style.left = left + 'px';
        profile_pic.style.top  = top  + 'px';
    }
}

function Get_Profile_Pic_Parameters()
{
    var profile_pic = document.getElementById('profile_pic');
    var offsets = profile_pic.getBoundingClientRect();

    var params = "{'image_width':      '" + parseInt(profile_pic.style.width.replace("px", "")) + "'," +
                   "'image_height':    '" + parseInt(profile_pic.style.height.replace("px", "")) + "'," +
                   "'crop_block_left': '" + Math.abs(offsets.left) + "'," +
                   "'crop_block_top':  '" + Math.abs(offsets.top) + "'," +
                   "'crop_block_dim':  '" + parseInt(document.getElementById('profile_pic_container').style.width.replace("px", "")) + "'}";

    PP_Editor_Android.Set_PP_Editor_Parameters(params);
}
