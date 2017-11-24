alias l="ls -al"
alias ll="ls -GFhl"
alias lp="ls -p"
alias h=history



export HISTSIZE=500
export HISTFILESIZE=1000
HISTFILE=$HOME/.bash_history

#export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home

export M2_HOME=/Users/scherian/store/Apps/apache-maven-3.2.3
#export JAVA_VERSION=1.8
export GROOVY_HOME=/usr/local/opt/groovy/libexec

export EDITOR=vim

#multiline prompt
PS1='
$PWD
==> '

export DISPLAY=:0.0
PATH=$PATH:/usr/X11R6/bin

#DevCustomization
ssh-add ~/.ssh/xxx.pem > /dev/null 2> /dev/null

#switch java version
alias j8='ju 1.8'
function ju() {
  export JAVA_HOME=$(/usr/libexec/java_home -v $1)
  export PATH=$JAVA_HOME/bin:$PATH
  java -version
}

function parse_git_branch () {
    git branch 2> /dev/null | sed -e '/^[^*]/d' -e 's/* \(.*\)/ (\1)/'
}

