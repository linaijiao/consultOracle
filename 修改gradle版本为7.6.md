{
 "cells": [
  {
   "cell_type": "markdown",
   "id": "e068130a-3204-412d-9d90-5b3d8f290001",
   "metadata": {},
   "source": [
    "# 修改gradle版本为7.6"
   ]
  },
  {
   "cell_type": "markdown",
   "id": "1e37ee24-fe3d-4fd5-b6cd-4e65f6175170",
   "metadata": {},
   "source": [
    "### 打开本地目录下文件\\consultOracle\\MyApp\\gradle\\wrapper\\gradle-wrapper.properties"
   ]
  },
  {
   "cell_type": "markdown",
   "id": "54a63cb7-0320-4c78-903a-ed7b68b095fb",
   "metadata": {},
   "source": [
    "### 改为 distributionUrl=https\\://services.gradle.org/distributions/gradle-7.6-bin.zip （版本号改为7.6）"
   ]
  },
  {
   "cell_type": "markdown",
   "id": "1c06db53-c314-4246-995d-9d18b8128415",
   "metadata": {},
   "source": [
    "### 在android studio的终端进入 \\consultOracle\\MyApp> 路径下执行./gradlew.bat -v   ，系统自动下载要更新的gradle版本"
   ]
  }
 ],
 "metadata": {
  "kernelspec": {
   "display_name": "Python 3 (ipykernel)",
   "language": "python",
   "name": "python3"
  },
  "language_info": {
   "codemirror_mode": {
    "name": "ipython",
    "version": 3
   },
   "file_extension": ".py",
   "mimetype": "text/x-python",
   "name": "python",
   "nbconvert_exporter": "python",
   "pygments_lexer": "ipython3",
   "version": "3.12.10"
  }
 },
 "nbformat": 4,
 "nbformat_minor": 5
}
