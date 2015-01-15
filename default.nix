let pkgs = import <nixpkgs> {};
in pkgs.myEnvFun {
        name = "openArpa-env";
        buildInputs = with pkgs.python27Packages; [
          python
          xlrd

        ];
    }



















