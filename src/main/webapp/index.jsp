<html>
<head>
    <link type="text/css" media="all" rel="stylesheet" href="css/bootstrap.css" />
    <link type="text/css" media="all" rel="stylesheet" href="css/main.css" />
</head>
<body>
<section class="container">
    <h2>Translator</h2>
    <form action="/compile" method="post" enctype="multipart/form-data">
        <span class="btn btn-primary btn-file">
            Browse..<input type="file" name="file">
        </span>
        <input class="btn btn-primary" type="submit" value="analyze">
    </form>
</section>
</body>
</html>
