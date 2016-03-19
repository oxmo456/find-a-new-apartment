Vagrant.configure(2) do |config|

  config.vm.box = "ubuntu/trusty64"

  config.vm.network "forwarded_port", guest: 6379, host: 6379

  config.vm.provision :shell, path: "vagrant-server-provision.sh"

  config.vm.provider "virtualbox" do |vb|
    vb.gui = false
    vb.memory = "1024"
  end

end
