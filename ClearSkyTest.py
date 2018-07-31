from appium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from selenium.common.exceptions import NoSuchElementException
from appium.webdriver.common.touch_action import TouchAction
import pytest

TIMEOUT = 30 #sec
LIST_SENSOR = ['DHT22', 'MQ-3']
# Warning: Update the name of the test before to run the script
testName = 'Test 1 - Welcome Slides'

# CAPABILITIES #
caps = {
    'platformName': 'Android',
    'deviceName': 'testAVD',
    'app': 'C:\\Users\\ftamagny\\StudioProjects\\Android\\app\\build\\outputs\\apk\\debug\\app-debug.apk',
    'avd': 'testAVD',
    'autoGrantPermissions': 'true',
    'automationName': 'UiAutomator2',
    'platformVersion': '5.1.0',
}

def get_element_by_id(id):
    element = None
    try:
        element = WebDriverWait(
            driver, (TIMEOUT / 2)).until(EC.presence_of_element_located((By.ID, id)))
    except:
        element = None
    return element

def get_element_by_path(path):
    element = None
    try:
        element = WebDriverWait(
            driver, (TIMEOUT / 2)).until(EC.presence_of_element_located((By.XPATH, id)))
    except:
        element = None
    return element

# Est-ce que c'est possible que ca marche pas ? Retry plusieurs fois si oui
driver = webdriver.Remote('http://localhost:4723/wd/hub', caps)

# Check if the activity of the application is OK
activity = driver.current_activity
assert activity == 'com.example.android.activities.WelcomeActivity'
# element = get_element_by_id('slider_image')
# print(str(element))
# TouchAction(driver).press(x=641, y=350).move_to(x=69, y=356).release().perform()
# TouchAction(driver).press(x=641, y=350).move_to(x=69, y=356).release().perform()

elems_navigation = driver.find_elements_by_class_name('android.support.v7.app.ActionBar$Tab')
elems_navigation[5].click()

start = WebDriverWait(driver, (TIMEOUT / 2)).until(EC.presence_of_element_located((By.ID, 'confirmSensors')))
start.click()
toast = WebDriverWait(driver, (TIMEOUT / 2)).until(EC.presence_of_element_located((By.XPATH, '//android.widget.Toast')))
assert toast is not None
# actions = TouchActions(driver)
# actions.flick_element(element, -100, 0, 200)
# actions.perform()

elems_navigation[2].click()

el_list = WebDriverWait(driver, (TIMEOUT / 2)).until(EC.visibility_of_element_located((By.ID, 'listSensors')))
for i in range(3):
    for el in el_list.find_elements_by_xpath('//android.view.View'):
        try:
            print(str(el))
            if el.find_element_by_id('nameSensor').text in LIST_SENSOR:
                el_switch = el.find_element_by_id('switchSensor')
                if el_switch.get_attribute('checked') == 'false':
                    el_switch.click()
        except NoSuchElementException as e:
            pass

    TouchAction(driver).press(x=el.location['x'] + el.size['width']/2, y=el.location['y'] - 10).move_to(x=el_list.location['x'] + el_list.size['width']/2, y = el_list.location['y']).release().perform() 
# TouchAction(driver).press(x=641, y=350).move_to(x=69, y=356).release().perform()
    

# start = WebDriverWait(driver, (TIMEOUT / 2)).until(EC.presence_of_element_located((By.ID, 'confirmSensors')))
# el1 = driver.find_element_by_xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.view.View/android.widget.FrameLayout[2]/android.view.View/android.support.v4.view.ViewPager/android.view.View/android.view.View/android.widget.ListView/android.view.View[2]/android.widget.Switch")
# el1.click()
# TouchAction(driver)   .press(x=387, y=889)   .move_to(x=390, y=836)   .release()   .perform()
    # 
# el2 = driver.find_element_by_xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.view.View/android.widget.FrameLayout[2]/android.view.View/android.support.v4.view.ViewPager/android.view.View/android.view.View/android.widget.ListView/android.view.View[4]/android.widget.Switch")
# el2.click()