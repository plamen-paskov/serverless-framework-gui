# Serverless Framework GUI
Integration of <a href="https://serverless.com/">Serverless Framework</a> for all JetBrains IDEs

The idea of the plugin in it's early stage is to ease and automate the tasks of switching between IDE/console while developing serverless applications with <a href="https://serverless.com/">Serverless Framework</a>.
The plugin adds a tool window at the right hand side of the IDE with label "Serverless Framework GUI". When you open the tool window you will see a list with all serverless.yaml or serverless.yml files found in your project. Under each service you will have every function found in the corresponding serverless.yaml file. Double clicking on a function name will deploy and invoke the selected function and the output of the commands will be displayed on a terminal window.

###### Requirements
To use this plugin you must have bash shell and <a href="https://serverless.com/">Serverless Framework</a> installed.

###### Notes
<b>serverless</b> executable must be in your search path