//
//  ViewController.swift
//  AEROPRO
//
//  Created by Smart Eye on 7/19/20.
//  Copyright © 2020 smarteye. All rights reserved.
//

import UIKit

class ViewController: UITableViewController {
    var arrayScan : [[String: Any?]]  = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationController?.navigationBar.tintColor = UIColor.black
        
//        // Add left navigation button
//        self.navigationItem.leftBarButtonItem = self.editButtonItem
        
        // Add right navigation button
        let scanButton = UIBarButtonItem(barButtonSystemItem: .camera, target: self, action: #selector(self.onClickScan(_:)))
        self.navigationItem.rightBarButtonItem = scanButton
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.getScanList()
    }

    @IBAction func onClickScan(_ sender: Any) {
        let scanVC = ScanViewController()
        let navControl = UINavigationController(rootViewController: scanVC)
        navControl.modalPresentationStyle = .fullScreen
        navControl.modalTransitionStyle = .crossDissolve
        present(navControl, animated: true, completion: nil)
    }
    
    func getScanList() {
        arrayScan = Utility.getDictionaryFromUserDefaults(key: USER_SCAN)
        if arrayScan.count == 0 {
            self.navigationItem.leftBarButtonItem?.isEnabled = false
        } else {
            self.navigationItem.leftBarButtonItem?.isEnabled = true
        }
        self.tableView.reloadData()
    }
    
    // MARK: - Table view data source
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 70
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.arrayScan.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "ListTableViewCell", for: indexPath) as? ListTableViewCell {
            let dic = arrayScan[indexPath.row]
            cell.nameLabel.text = (dic["title"] as? String ?? "")
            cell.timeLabel.text = (dic["date"] as? String ?? "")
            return cell
        }
        return UITableViewCell()
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let dic = arrayScan[indexPath.row]
        let link = dic["link"] as? String ?? ""
        if let url = URL(string: link){
            UIApplication.shared.open(url)
        }
    }
    
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            self.arrayScan.remove(at: indexPath.row)
            Utility.saveDictionaryToUserDefaults(value: arrayScan, key: USER_SCAN)
            
            if arrayScan.count != 0 {
                tableView.deleteRows(at: [indexPath], with: .fade)
            } else {
                self.viewWillAppear(true)
            }
        }
    }
}

