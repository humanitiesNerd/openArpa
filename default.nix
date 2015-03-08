      with import <nixpkgs> {};
      
      buildPythonPackage {
        name = "myproject";
      
        buildInputs = with pkgs.pythonPackages; [ xlrd ];
      
        src = ./.;
      }



















